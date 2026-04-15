package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.model.imports.AugmentationImport;
import com.eq.charactertracker.model.imports.CharacterImportInfo;
import com.eq.charactertracker.model.imports.CharacterImportRequest;
import com.eq.charactertracker.model.imports.EquippedItemImport;
import com.eq.charactertracker.model.imports.ImportResult;
import com.eq.charactertracker.model.imports.InventoryItemImport;
import com.eq.charactertracker.repo.ArmorRepo;
import com.eq.charactertracker.repo.AugmentationRepo;
import com.eq.charactertracker.repo.CharacterInventoryRepo;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.EquipSlotRepo;
import com.eq.charactertracker.repo.ItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private final UserService userService;
    private final ServerService serverService;
    private final CharacterRepo characterRepo;
    private final ArmorRepo armorRepo;
    private final AugmentationRepo augmentationRepo;
    private final EquipSlotRepo equipSlotRepo;
    private final ItemRepo itemRepo;
    private final CharacterInventoryRepo characterInventoryRepo;

    @Transactional
    public List<ImportResult> importCharacters(List<CharacterImportRequest> requests) {
        List<ImportResult> results = new ArrayList<>();
        for (CharacterImportRequest request : requests) {
            results.add(importCharacter(request));
        }
        return results;
    }

    private ImportResult importCharacter(CharacterImportRequest request) {
        UserEntity user = userService.getAuthenticatedUser();
        ServerEntity server = serverService.findByShortName(request.getCharacter().getServer());

        CharacterEntity character = upsertCharacter(request.getCharacter(), server, user);
        clearCharacterData(character, server);

        ImportResult result = new ImportResult();
        result.setCharacter(character.getName());
        result.setServer(server.getName());

        if (request.getEquipped() != null) {
            for (EquippedItemImport item : request.getEquipped()) {
                try {
                    processEquippedItem(item, character, server);
                    result.setEquippedImported(result.getEquippedImported() + 1);
                } catch (IllegalArgumentException e) {
                    String msg = String.format("Skipped equipped [%s] slot=%s extId=%d: %s",
                            item.getName(), item.getSlot(), item.getExternalId(), e.getMessage());
                    log.warn(msg);
                    result.addSkipped(msg);
                }
            }
        }

        if (request.getInventory() != null) {
            for (InventoryItemImport item : request.getInventory()) {
                try {
                    processInventoryItem(item, character, server);
                    result.setInventoryImported(result.getInventoryImported() + 1);
                } catch (IllegalArgumentException e) {
                    String msg = String.format("Skipped inventory [%s] slot=%s extId=%d: %s",
                            item.getName(), item.getSlot(), item.getExternalId(), e.getMessage());
                    log.warn(msg);
                    result.addSkipped(msg);
                }
            }
        }

        return result;
    }

    private void clearCharacterData(CharacterEntity character, ServerEntity server) {
        List<CharacterEquipSlotEntity> slots = equipSlotRepo
                .findCharacterEquipSlotEntitiesByCharacter_IdAndCharacter_Server(character.getId(), server);
        if (!slots.isEmpty()) {
            equipSlotRepo.deleteAllInBatch(slots);
        }

        List<CharacterInventoryEntity> inventory = characterInventoryRepo
                .findCharacterInventoryEntitiesByCharacter_Id(character.getId());
        if (!inventory.isEmpty()) {
            characterInventoryRepo.deleteAllInBatch(inventory);
        }
    }

    private CharacterEntity upsertCharacter(CharacterImportInfo info, ServerEntity server, UserEntity user) {
        CharacterEntity character = characterRepo.findCharacterEntityByUserIdAndNameAndServer(
                user.getId(), info.getName(), server);
        if (character == null) {
            character = new CharacterEntity();
            character.setName(info.getName());
            character.setServer(server);
            character.setUser(user);
        }
        character.setCharacterClass(CharacterClass.fromAbbreviation(info.getCharacterClass()));
        character.setLevel(info.getLevel());
        character.setAaSpent(info.getAaSpent());
        character.setAaAvailable(info.getAaAvailable());
        return characterRepo.save(character);
    }

    private void processEquippedItem(EquippedItemImport item, CharacterEntity character, ServerEntity server) {
        EquipSlotEnum slot = EquipSlotEnum.fromImportSlot(item.getSlot());
        ArmorEntity armor = upsertArmor(item, server);

        AugmentationEntity aug1 = null, aug2 = null, aug3 = null;
        if (item.getAugs() != null) {
            for (AugmentationImport aug : item.getAugs()) {
                AugmentationEntity augEntity = upsertAugmentation(aug, server);
                if (aug.getSlot() == 1) aug1 = augEntity;
                else if (aug.getSlot() == 2) aug2 = augEntity;
                else if (aug.getSlot() == 3) aug3 = augEntity;
                else log.warn("Aug slot {} not supported, skipping aug {} on item {}", aug.getSlot(), aug.getName(), item.getName());
            }
        }

        CharacterEquipSlotEntity equipSlot = CharacterEquipSlotEntity.builder()
                .character(character)
                .equipSlot(slot)
                .build();
        equipSlot.setArmor(armor);
        equipSlot.setAug1(aug1);
        equipSlot.setAug2(aug2);
        equipSlot.setAug3(aug3);
        equipSlotRepo.save(equipSlot);
    }

    private ArmorEntity upsertArmor(EquippedItemImport item, ServerEntity server) {
        ArmorEntity armor = armorRepo.findArmorEntityByExtIdAndServer(item.getExternalId(), server)
                .orElse(new ArmorEntity());
        armor.setExtId(item.getExternalId());
        armor.setServer(server);
        armor.setName(item.getName());
        armor.setAc(nvl(item.getAc()));
        armor.setStrength(nvl(item.getStr()));
        armor.setStamina(nvl(item.getSta()));
        armor.setAgility(nvl(item.getAgi()));
        armor.setDexterity(nvl(item.getDex()));
        armor.setWisdom(nvl(item.getWis()));
        armor.setIntelligence(nvl(item.getIntelligence()));
        armor.setCharisma(nvl(item.getCha()));
        armor.setHitPoints(nvl(item.getHp()));
        armor.setManaPoints(nvl(item.getMana()));
        armor.setFireResist(nvl(item.getSvFire()));
        armor.setColdResist(nvl(item.getSvCold()));
        armor.setMagicResist(nvl(item.getSvMagic()));
        armor.setDiseaseResist(nvl(item.getSvDisease()));
        armor.setPoisonResist(nvl(item.getSvPoison()));
        armor.setAttack(nvl(item.getAttack()));
        armor.setHeroicStrength(nvl(item.getHeroicStr()));
        armor.setHeroicStamina(nvl(item.getHeroicSta()));
        armor.setHeroicAgility(nvl(item.getHeroicAgi()));
        armor.setHeroicDexterity(nvl(item.getHeroicDex()));
        armor.setHeroicWisdom(nvl(item.getHeroicWis()));
        armor.setHeroicIntelligence(nvl(item.getHeroicInt()));
        armor.setHeroicCharisma(nvl(item.getHeroicCha()));
        armor.setEndurancePoints(0);
        armor.setAccuracy(0.0);
        armor.setAvoidance(0.0);
        armor.setCombatEffect(0.0);
        armor.setStunResist(0.0);
        armor.setDamageShield(0);
        armor.setDotShielding(0.0);
        armor.setEnduranceRegen(0);
        armor.setHitPointsRegen(0);
        armor.setManaPointsRegen(0);
        armor.setSpellShielding(0.0);
        armor.setShielding(0.0);
        armor.setStrikethrough(0.0);
        return armorRepo.save(armor);
    }

    private AugmentationEntity upsertAugmentation(AugmentationImport aug, ServerEntity server) {
        AugmentationEntity entity = augmentationRepo.findByExtIdAndServer(aug.getExternalId(), server)
                .orElse(new AugmentationEntity());
        entity.setExtId(aug.getExternalId());
        entity.setServer(server);
        entity.setName(aug.getName());
        entity.setAc(nvl(aug.getAc()));
        entity.setStrength(nvl(aug.getStr()));
        entity.setStamina(nvl(aug.getSta()));
        entity.setAgility(nvl(aug.getAgi()));
        entity.setDexterity(nvl(aug.getDex()));
        entity.setWisdom(nvl(aug.getWis()));
        entity.setIntelligence(nvl(aug.getIntelligence()));
        entity.setCharisma(nvl(aug.getCha()));
        entity.setHitPoints(nvl(aug.getHp()));
        entity.setManaPoints(nvl(aug.getMana()));
        entity.setFireResist(nvl(aug.getSvFire()));
        entity.setColdResist(nvl(aug.getSvCold()));
        entity.setMagicResist(nvl(aug.getSvMagic()));
        entity.setDiseaseResist(nvl(aug.getSvDisease()));
        entity.setPoisonResist(nvl(aug.getSvPoison()));
        entity.setAttack(nvl(aug.getAttack()));
        entity.setHeroicStrength(0);
        entity.setHeroicStamina(0);
        entity.setHeroicAgility(0);
        entity.setHeroicDexterity(0);
        entity.setHeroicWisdom(0);
        entity.setHeroicIntelligence(0);
        entity.setHeroicCharisma(0);
        entity.setEndurancePoints(0);
        entity.setAccuracy(0.0);
        entity.setAvoidance(0.0);
        entity.setCombatEffect(0.0);
        entity.setStunResist(0.0);
        entity.setDamageShield(0);
        entity.setDotShielding(0.0);
        entity.setEnduranceRegen(0);
        entity.setHitPointsRegen(0);
        entity.setManaPointsRegen(0);
        entity.setSpellShielding(0.0);
        entity.setShielding(0.0);
        entity.setStrikethrough(0.0);
        return augmentationRepo.save(entity);
    }

    private void processInventoryItem(InventoryItemImport item, CharacterEntity character, ServerEntity server) {
        ItemEntity itemEntity = itemRepo.findItemEntityByExternalIdAndServer(item.getExternalId(), server)
                .orElse(ItemEntity.builder()
                        .externalId(item.getExternalId())
                        .server(server)
                        .build());
        itemEntity.setName(item.getName());
        itemEntity = itemRepo.save(itemEntity);

        CharacterInventoryEntity inventory = CharacterInventoryEntity.builder()
                .slotName(item.getSlot())
                .character(character)
                .item(itemEntity)
                .build();
        characterInventoryRepo.save(inventory);
    }

    private int nvl(Integer value) {
        return value == null ? 0 : value;
    }
}
