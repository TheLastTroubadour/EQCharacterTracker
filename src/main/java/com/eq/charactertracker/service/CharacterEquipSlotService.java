package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.CharacterEquipSlotDTO;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.repo.EquipSlotRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CharacterEquipSlotService extends BaseServiceImpl<CharacterEquipSlotEntity, CharacterEquipSlotDTO> {

    private final EquipSlotRepo equipSlotRepo;
    private final ServerService serverService;

    public CharacterEquipSlotService(ModelMapper modelMapper, EquipSlotRepo equipSlotRepo, ServerService serverService) {
        super(modelMapper, equipSlotRepo);
        this.equipSlotRepo = equipSlotRepo;
        this.serverService = serverService;
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

    public CharacterEquipSlotDTO save(CharacterEquipSlotDTO characterEquipSlot) {
        CharacterEquipSlotEntity characterEquipSlotEntity = dtoToEntity(characterEquipSlot);
        characterEquipSlotEntity = equipSlotRepo.save(characterEquipSlotEntity);
        return entityToDTO(characterEquipSlotEntity);
    }
}
