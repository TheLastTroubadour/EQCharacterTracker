package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.entity.spell.SpellEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.model.UserDTO;
import com.eq.charactertracker.model.imports.AugmentationImport;
import com.eq.charactertracker.model.imports.CharacterImportInfo;
import com.eq.charactertracker.model.imports.CharacterImportRequest;
import com.eq.charactertracker.model.imports.EquippedItemImport;
import com.eq.charactertracker.model.imports.ImportResult;
import com.eq.charactertracker.model.imports.InventoryItemImport;
import com.eq.charactertracker.model.imports.SpellImport;
import com.eq.charactertracker.repo.ArmorRepo;
import com.eq.charactertracker.repo.AugmentationRepo;
import com.eq.charactertracker.repo.CharacterInventoryRepo;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.EquipSlotRepo;
import com.eq.charactertracker.repo.ItemRepo;
import com.eq.charactertracker.repo.spell.CharacterSpellRepo;
import com.eq.charactertracker.repo.spell.SpellRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    // The live-game exporter isn't consistent about the space after "Rk." (seen both
    // "Rk. III" and "Rk.III"), so it's optional here.
    private static final Pattern RANK_SUFFIX_PATTERN = Pattern.compile("^(.*) Rk\\.\\s*(II|III)$");

    private final UserService userService;
    private final ServerService serverService;
    // TODO: route through CharacterService instead of calling CharacterRepo directly
    private final CharacterRepo characterRepo;
    // TODO: route through ArmorService instead of calling ArmorRepo directly
    private final ArmorRepo armorRepo;
    // TODO: no AugmentationService exists yet -- calling AugmentationRepo directly
    private final AugmentationRepo augmentationRepo;
    // TODO: route through CharacterEquipSlotService instead of calling EquipSlotRepo directly
    private final EquipSlotRepo equipSlotRepo;
    // TODO: no ItemService exists yet -- calling ItemRepo directly
    private final ItemRepo itemRepo;
    // TODO: no CharacterInventoryService exists yet -- calling CharacterInventoryRepo directly
    private final CharacterInventoryRepo characterInventoryRepo;
    // TODO: route through SpellService instead of calling SpellRepo directly
    private final SpellRepo spellRepo;
    // TODO: route through CharacterSpellService instead of calling CharacterSpellRepo directly
    private final CharacterSpellRepo characterSpellRepo;
    private final CharacterService characterService;
    private final ModelMapper modelMapper;

    @Transactional
    public List<ImportResult> importCharacters(List<CharacterImportRequest> requests) {
        List<ImportResult> results = new ArrayList<>();
        for (CharacterImportRequest request : requests) {
            results.add(importCharacter(request));
        }
        return results;
    }

    private ImportResult importCharacter(CharacterImportRequest request) {
        UserDTO user = userService.getAuthenticatedUser();
        ServerDTO serverDTO = serverService.findByName(request.getCharacter().getServer());
        ServerEntity server = modelMapper.map(serverDTO, ServerEntity.class);

        CharacterDTO characterDTO = upsertCharacter(request.getCharacter(), serverDTO, user);
        CharacterEntity character = modelMapper.map(characterDTO, CharacterEntity.class);
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

        // Disciplines live in the same catalog/progress tables as spells (see
        // CharacterService.SpellUnlockType discussion) -- there's no separate "type" field, so
        // they're processed through the exact same processSpell logic, just from a different
        // section of the export. Reset once, before either list, so a discipline processed
        // first doesn't get wiped out by a spells-triggered reset run afterward.
        if (request.getSpells() != null || request.getDisciplines() != null) {
            characterSpellRepo.resetScribedForCharacter(character.getId());
        }

        if (request.getSpells() != null) {
            for (SpellImport spell : request.getSpells()) {
                try {
                    result.setSpellsImported(result.getSpellsImported() + processSpell(spell, character, result));
                } catch (IllegalArgumentException e) {
                    String msg = String.format("Skipped spell [%s]: %s", spell.getName(), e.getMessage());
                    log.warn(msg);
                    result.addSkipped(msg);
                }
            }
        }

        if (request.getDisciplines() != null) {
            for (SpellImport discipline : request.getDisciplines()) {
                try {
                    result.setSpellsImported(result.getSpellsImported() + processSpell(discipline, character, result));
                } catch (IllegalArgumentException e) {
                    String msg = String.format("Skipped discipline [%s]: %s", discipline.getName(), e.getMessage());
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

    private CharacterDTO upsertCharacter(CharacterImportInfo info, ServerDTO server, UserDTO user) {
        CharacterDTO character = characterService.findCharacterByUserIdAndNameAndServer(user.getId(), info.getName(), server);
        if (character == null) {
            character = new CharacterDTO ();
            character.setName(info.getName());
            character.setServer(server);
            character.setUser(user);
        }
        character.setCharacterClass(CharacterClass.fromAbbreviation(info.getCharacterClass()));
        character.setLevel(info.getLevel());
        character.setAaSpent(info.getAaSpent());
        character.setAaAvailable(info.getAaAvailable());
        return characterService.create(character);
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

    // name/rankName carry the rank suffix when present (e.g. "Cloak of Corruption Rk. II").
    // The export's suffix format varies ("Rk. II" / "Rk.II"); the catalog's is canonical
    // "<base> Rk.II" / "<base> Rk.III" (rank 1 is the bare base name). Parse the base name +
    // max rank off the export string, then rebuild the canonical per-rank name for the lookup.
    // Scribing rank 2 replaces rank 1 in the spellbook, rank 3 replaces rank 2 -- so having
    // rank N scribed means every rank below N was scribed too, and we mark all of them.
    private int processSpell(SpellImport spellImport, CharacterEntity character, ImportResult result) {
        ServerType serverType = character.getServer().getType();
        String rankName = spellImport.getRankName() != null ? spellImport.getRankName() : spellImport.getName();
        rankName = normalizeSpellName(rankName);

        String baseName = rankName;
        short maxRank = 1;
        Matcher m = RANK_SUFFIX_PATTERN.matcher(rankName);
        if (m.matches()) {
            baseName = m.group(1);
            maxRank = m.group(2).equals("II") ? (short) 2 : (short) 3;
        }

        int marked = 0;
        for (short rank = 1; rank <= maxRank; rank++) {
            String lookupName = catalogName(baseName, rank);
            short currentRank = rank;
            boolean isImportedRank = rank == maxRank;
            SpellEntity spellEntity = spellRepo
                    .findByNameAndRankAndCharacterClassAndServerType(lookupName, rank, character.getCharacterClass(), serverType)
                    .orElseGet(() -> createSpellFromImport(lookupName, currentRank, character.getCharacterClass(), serverType,
                            spellImport, isImportedRank, result));

            CharacterSpellEntity progress = characterSpellRepo.findByCharacter_IdAndSpell_Id(character.getId(), spellEntity.getId())
                    .orElseGet(() -> {
                        CharacterSpellEntity created = new CharacterSpellEntity();
                        created.setCharacter(character);
                        created.setSpell(spellEntity);
                        return created;
                    });
            progress.setScribed(true);
            characterSpellRepo.save(progress);
            marked++;
        }
        return marked;
    }

    // Auto-creates a minimal catalog entry when the spellbook/discipline list references a
    // spell not yet in the catalog, using only what the character export provides. The export
    // only carries level/mana for the rank the character actually has scribed -- for a rank N
    // spell that implies ranks 1..N-1 too (see processSpell), those lower ranks get created
    // with placeholder level/mana rather than guessed values, since the export has no data for
    // them. Either way, description/expansion/unlockType/rune data are never in the export, so
    // every auto-created row is flagged in the result for a manual catalog pass.
    private SpellEntity createSpellFromImport(String name, short rank, CharacterClass characterClass,
                                               ServerType serverType, SpellImport spellImport,
                                               boolean isImportedRank, ImportResult result) {
        SpellEntity spell = new SpellEntity();
        spell.setName(name);
        spell.setRank(rank);
        spell.setCharacterClass(characterClass);
        spell.setServerType(serverType);
        spell.setLevel(isImportedRank && spellImport.getLevel() != null ? spellImport.getLevel() : 0);
        spell.setManaCost(isImportedRank ? spellImport.getMana() : null);
        spell.setEnduranceCost(isImportedRank ? spellImport.getEnduranceCost() : null);
        spell.setNeedsReview(true);
        spell = spellRepo.save(spell);

        result.addNeedsReview(String.format(
                "Auto-created spell [%s] rank %d (%s, %s) from import -- needs manual review "
                        + "(description/expansion/unlock data missing%s)",
                name, rank, characterClass, serverType,
                isImportedRank ? "" : "; level/mana are placeholders, not in the export for this lower rank"));
        return spell;
    }

    // Lucy_Data (the catalog source) never has colons in spell names, but the live-game
    // exporter does -- strip them so the catalog lookup still matches.
    private String normalizeSpellName(String name) {
        return name.replace(":", "").replaceAll(" {2,}", " ").trim();
    }

    // The catalog stores rank 1 under the bare base name and ranks 2/3 with a canonical
    // " Rk.II" / " Rk.III" suffix -- see processSpell and the rank-suffix data migration.
    private String catalogName(String baseName, short rank) {
        return switch (rank) {
            case 2 -> baseName + " Rk.II";
            case 3 -> baseName + " Rk.III";
            default -> baseName;
        };
    }

    private int nvl(Integer value) {
        return value == null ? 0 : value;
    }
}
