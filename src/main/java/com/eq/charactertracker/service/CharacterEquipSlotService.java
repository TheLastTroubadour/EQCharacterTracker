package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.repo.EquipSlotRepo;
import com.eq.charactertracker.entity.CharacterEquipSlotEntity;
import com.eq.charactertracker.model.CharacterEquipSlot;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterEquipSlotService {

    private final EquipSlotRepo equipSlotRepo;
    private final ModelMapper modelMapper;

    public List<CharacterEquipSlot> searchCharactersByClassesAndSlotsAndServer(List<CharacterClass> classes, List<EquipSlotEnum> slots, ServerEnum serverEnum) {
        if (classes == null || classes.size() == 0) {
            classes = Arrays.stream(CharacterClass.values()).collect(Collectors.toList());
        }
        if (slots == null || slots.size() == 0) {
            slots = Arrays.stream(EquipSlotEnum.values()).collect(Collectors.toList());
        }
        List<CharacterEquipSlotEntity> entities;
        if (serverEnum != null) {
            entities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlotAndServer(classes, slots, serverEnum);
        } else{
            entities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_CharacterClassAndEquipSlot(classes, slots);
        }
        return convertListofCharacterEquipSlotEntityToModel(entities);
    }

    public List<CharacterEquipSlot> getAllCharacterArmorSlots() {
        List<CharacterEquipSlotEntity> entities = equipSlotRepo.findAll();
        return convertListofCharacterEquipSlotEntityToModel(entities);
    }

    private List<CharacterEquipSlot> convertListofCharacterEquipSlotEntityToModel(List<CharacterEquipSlotEntity> l1){
        List<CharacterEquipSlot> characterEquipSlotList = new ArrayList<>();
        for(CharacterEquipSlotEntity entity: l1){
            characterEquipSlotList.add(modelMapper.map(entity, CharacterEquipSlot.class));
        }
        return characterEquipSlotList;
    }

    public Optional<CharacterEquipSlot> findCharacterEquipSlotEntityByCharacterAndEquipSlot(Character character, EquipSlotEnum slotEnum) {
        Optional<CharacterEquipSlotEntity> characterEquipSlotEntity = equipSlotRepo.findCharacterEquipSlotEntityByCharacter_IdAndEquipSlot(character.getId(), slotEnum);
        if(characterEquipSlotEntity.isPresent()) {
            CharacterEquipSlot characterEquipSlot = modelMapper.map(characterEquipSlotEntity.get(), CharacterEquipSlot.class);
            return Optional.ofNullable(characterEquipSlot);
        }
        return Optional.empty();
    }

    public List<CharacterEquipSlot> findCharacterEquipSlotEntitiesByCharacterIdAndCharacterServer(Long characterId, ServerEnum serverName) {
       List<CharacterEquipSlotEntity> characterEquipSlotEntities = equipSlotRepo.findCharacterEquipSlotEntitiesByCharacter_IdAndCharacter_Server(characterId, serverName);
       return characterEquipSlotEntities.stream().map(e -> modelMapper.map(e, CharacterEquipSlot.class)).collect(Collectors.toList());
    }

    public CharacterEquipSlot save(CharacterEquipSlot characterEquipSlot){
        CharacterEquipSlotEntity characterEquipSlotEntity = modelMapper.map(characterEquipSlot, CharacterEquipSlotEntity.class);
        characterEquipSlotEntity = equipSlotRepo.save(characterEquipSlotEntity);
        return modelMapper.map(characterEquipSlotEntity, CharacterEquipSlot.class);
    }
}
