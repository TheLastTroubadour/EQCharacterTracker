package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.ArmorDTO;
import com.eq.charactertracker.model.AugMatchDTO;
import com.eq.charactertracker.model.AugPresenceDTO;
import com.eq.charactertracker.model.Augmentation;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.CharacterEquipSlotDTO;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.EquipSlotRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CharacterEquipSlotService extends BaseServiceImpl<CharacterEquipSlotEntity, CharacterEquipSlotDTO> {

    private final EquipSlotRepo equipSlotRepo;
    private final ServerService serverService;
    // TODO: route through CharacterService instead of calling CharacterRepo directly
    private final CharacterRepo characterRepo;

    public CharacterEquipSlotService(ModelMapper modelMapper, EquipSlotRepo equipSlotRepo, ServerService serverService,
                                      CharacterRepo characterRepo) {
        super(modelMapper, equipSlotRepo);
        this.equipSlotRepo = equipSlotRepo;
        this.serverService = serverService;
        this.characterRepo = characterRepo;
    }

    public List<CharacterEquipSlotDTO> searchCharactersByClassesAndSlotsAndServer(List<CharacterClass> classes, List<EquipSlotEnum> slots, String serverShortName) {
        if (classes == null || classes.isEmpty()) {
            classes = Arrays.stream(CharacterClass.values()).collect(Collectors.toList());
        }
        if (slots == null || slots.isEmpty()) {
            slots = Arrays.stream(EquipSlotEnum.values()).collect(Collectors.toList());
        }
        List<CharacterEquipSlotEntity> entities;
        if (serverShortName != null) {
            ServerDTO server = serverService.findByName(serverShortName);
            entities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlotAndServer(classes, slots, getModelMapper().map(server, ServerEntity.class));
        } else {
            entities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlot(classes, slots);
        }
        return entities.stream().map(this::entityToDTO).toList();
    }

    public List<CharacterEquipSlotDTO> getAllCharacterArmorSlots() {
        return equipSlotRepo.findAll().stream().map(this::entityToDTO).toList();
    }

    public Optional<CharacterEquipSlotDTO> findCharacterEquipSlotEntityByCharacterAndEquipSlot(CharacterDTO character, EquipSlotEnum slotEnum) {
        return equipSlotRepo.findCharacterEquipSlotEntityByCharacter_IdAndEquipSlot(character.getId(), slotEnum)
                .map(this::entityToDTO);
    }

    public List<CharacterEquipSlotDTO> findCharacterEquipSlotEntitiesByCharacterIdAndCharacterServer(Long characterId, String serverShortName) {
        ServerDTO server = serverService.findByName(serverShortName);
        List<CharacterEquipSlotEntity> characterEquipSlotEntities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_IdAndCharacter_Server(characterId, getModelMapper().map(server, ServerEntity.class));
        return characterEquipSlotEntities.stream().map(this::entityToDTO).toList();
    }

    // Splits characters (filtered by classes/server, same defaulting rules as
    // searchCharactersByClassesAndSlotsAndServer) into who has a given aug equipped
    // (aug1/aug2/aug3, name-contains match) vs. who doesn't. "Doesn't have" is computed as the
    // filtered candidate set minus the has-aug set, not a separate query -- a character with
    // zero equip slots imported at all still counts as "missing" the aug. A character can match
    // more than once (same aug in two slots, or two differently-named augs both containing the
    // search text), so each has-aug entry carries the distinct matching aug name(s) alongside
    // the character.
    public AugPresenceDTO searchAugPresence(String augName, List<CharacterClass> classes, String serverShortName) {
        if (augName == null || augName.isBlank()) {
            throw new IllegalArgumentException("augName is required");
        }
        if (classes == null || classes.isEmpty()) {
            classes = Arrays.stream(CharacterClass.values()).collect(Collectors.toList());
        }
        ServerEntity server = serverShortName != null
                ? getModelMapper().map(serverService.findByName(serverShortName), ServerEntity.class)
                : null;

        String lowerAugName = augName.toLowerCase();
        String namePattern = "%" + lowerAugName + "%";
        List<CharacterEntity> candidates = characterRepo.findByCharacterClassInAndServer(classes, server);
        List<CharacterEquipSlotEntity> matchingSlots = equipSlotRepo.findEquipSlotsWithAugNameMatching(namePattern, classes, server);

        Map<Long, CharacterEntity> charactersById = new LinkedHashMap<>();
        Map<Long, Set<String>> augNamesByCharacterId = new LinkedHashMap<>();
        for (CharacterEquipSlotEntity slot : matchingSlots) {
            CharacterEntity character = slot.getCharacter();
            charactersById.putIfAbsent(character.getId(), character);
            Set<String> matchedNames = augNamesByCharacterId.computeIfAbsent(character.getId(), id -> new LinkedHashSet<>());
            // Arrays.asList, not List.of -- List.of throws NPE on a null element, and aug2/aug3
            // being null is the common case (most items only fill 1-2 of their 3 aug slots).
            for (AugmentationEntity aug : Arrays.asList(slot.getAug1(), slot.getAug2(), slot.getAug3())) {
                if (aug != null && aug.getName() != null && aug.getName().toLowerCase().contains(lowerAugName)) {
                    matchedNames.add(aug.getName());
                }
            }
        }

        AugPresenceDTO result = new AugPresenceDTO();
        result.setHasAug(charactersById.values().stream()
                .map(c -> new AugMatchDTO(
                        getModelMapper().map(c, CharacterDTO.class),
                        List.copyOf(augNamesByCharacterId.get(c.getId()))))
                .toList());
        result.setMissingAug(candidates.stream()
                .filter(c -> !charactersById.containsKey(c.getId()))
                .map(c -> getModelMapper().map(c, CharacterDTO.class))
                .toList());
        return result;
    }

    public CharacterEquipSlotDTO save(CharacterEquipSlotDTO characterEquipSlot) {
        CharacterEquipSlotEntity characterEquipSlotEntity = dtoToEntity(characterEquipSlot);
        characterEquipSlotEntity = equipSlotRepo.save(characterEquipSlotEntity);
        return entityToDTO(characterEquipSlotEntity);
    }

    // Overrides the inherited entityToDTO -- mapping the whole CharacterEquipSlotEntity graph
    // in one modelMapper.map() call doesn't reliably apply the registered ArmorEntity->ArmorDTO
    // / AugmentationEntity->Augmentation converters once those entities are nested this deep
    // (armor.server ends up as ArmorEntity's raw toString() instead of the shortName string --
    // ModelMapper falls back to its default Object-to-String converter). Single-object
    // modelMapper.map() calls on each nested entity apply the registered converters correctly
    // (same fix NpcService.entityToDTO uses for its own nested collections), so each field is
    // mapped explicitly here instead of relying on one bulk graph conversion.
    @Override
    protected CharacterEquipSlotDTO entityToDTO(CharacterEquipSlotEntity entity) {
        CharacterEquipSlotDTO dto = new CharacterEquipSlotDTO();
        dto.setId(entity.getId());
        dto.setEquipSlot(entity.getEquipSlot());
        dto.setCharacter(getModelMapper().map(entity.getCharacter(), CharacterDTO.class));
        dto.setArmor(getModelMapper().map(entity.getArmor(), ArmorDTO.class));
        dto.setAug1(entity.getAug1() != null ? getModelMapper().map(entity.getAug1(), Augmentation.class) : null);
        dto.setAug2(entity.getAug2() != null ? getModelMapper().map(entity.getAug2(), Augmentation.class) : null);
        dto.setAug3(entity.getAug3() != null ? getModelMapper().map(entity.getAug3(), Augmentation.class) : null);
        return dto;
    }
}
