package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.Epic1_5_RequiredItemsEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.quest.thf.Epic1_5QuestEntity;
import com.eq.charactertracker.entity.quest.thf.QuestItemEntity;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.quest.thf.Epic1_5Quest;
import com.eq.charactertracker.repo.CharacterInventoryRepo;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.Epic1_5Repo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class Epic1_5Service {

    private final Epic1_5Repo repo;
    private final ModelMapper modelMapper;

    private final Epic1_5Repo epic1_5Repo;
    private final CharacterInventoryRepo characterInventoryRepo;
    private final THFItemService thfItemService;
    private final CharacterService characterService;
    private final CharacterRepo characterRepo;

    public List<Epic1_5Quest> getAllByUserId(Long userId) {
        List<Epic1_5QuestEntity> entities = repo.findAllByCharacter_User_Id(userId);
        List<Epic1_5Quest> models = new ArrayList<>();
        entities.forEach(e -> {
            models.add(modelMapper.map(e, Epic1_5Quest.class));
        });
        return models;
    }

    public List<Epic1_5Quest> getAll() {
        List<Epic1_5QuestEntity> entities = repo.findAllByOrderByCompleted();
        List<Epic1_5Quest> models = new ArrayList<>();
        entities.forEach(e -> {
            models.add(modelMapper.map(e, Epic1_5Quest.class));
        });
        return models;
    }

    public List<Epic1_5Quest> updateAllEpic1_5Quest(){
        List<Character> characterList = characterService.getAllCharacters();
        //List<Epic1_5Quest> epic15QuestList = new ArrayList<>();
        List<Epic1_5Quest> epic15QuestList = characterList.stream().map(e -> updateEpic1_5Quest(e)).collect(Collectors.toList());
        characterList.forEach(c -> epic15QuestList.add(updateEpic1_5Quest(c)));
        return epic15QuestList;
    }

    public Epic1_5Quest updateEpic1_5Quest(Character character){
        Optional<Epic1_5QuestEntity> optional = epic1_5Repo.findEpic1_5QuestEntityByCharacterId(character.getId());
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByNameAndServer(character.getName(), ServerEnum.THF);
        Epic1_5QuestEntity entity = null;
        boolean has1_5Epic = false;
        if(optional.isEmpty()){
            entity = new Epic1_5QuestEntity();
            entity.setCharacter(characterEntity);
            initAllQuestItemEntity(entity);
        }else{
            entity = optional.get();
        }
        has1_5Epic = findIfCharacterHas1_5Epic(character);
        entity.setCompleted(has1_5Epic);
        setAllQuestItemEntityFields(characterEntity, entity, has1_5Epic);
        epic1_5Repo.save(entity);

        return modelMapper.map(entity, Epic1_5Quest.class);
    }

    private void initAllQuestItemEntity(Epic1_5QuestEntity epic1_5QuestEntity){
        Field[] fields = epic1_5QuestEntity.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object obj = f.get(epic1_5QuestEntity);
                if(f.getType().isAssignableFrom(QuestItemEntity.class)){
                    QuestItemEntity questItemEntity = (QuestItemEntity) obj;
                    if(questItemEntity == null){
                        questItemEntity= new QuestItemEntity();
                        f.set(epic1_5QuestEntity, questItemEntity);
                    }
                    ItemEntity item = thfItemService.createOrGetItemEntity(Epic1_5_RequiredItemsEnum.lookUpByFieldName(f.getName()).getExternalId(), ServerEnum.THF);
                    questItemEntity.setItem(item);
                }
            } catch (IllegalAccessException e) {
                log.error(String.format("Unable to access field on %s with field name %s",f, f.getName()));
            }
            f.setAccessible(false);
        }

    }
    private void setAllQuestItemEntityFields(CharacterEntity character, Epic1_5QuestEntity epic1_5QuestEntity, boolean isCompleted){
        Field[] fields = epic1_5QuestEntity.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object obj = f.get(epic1_5QuestEntity);
                if(obj != null && obj instanceof QuestItemEntity){
                    QuestItemEntity questItemEntity = (QuestItemEntity) obj;
                    if(isCompleted){
                        questItemEntity.setInInventory(true);
                    }else {
                        questItemEntity.setInInventory(findIfCharacterItemInInventory(character, questItemEntity));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(String.format("Unable to access field on %s with field name %s on character %s",f, f.getName(), character.getName()));
            }
            f.setAccessible(false);
        }

    }

    private Boolean findIfCharacterItemInInventory(CharacterEntity character, QuestItemEntity entity){
        ItemEntity item = thfItemService.createOrGetItemEntity(entity.getItem().getExternalId(), entity.getItem().getServer());
        Optional<CharacterInventoryEntity> characterInventoryEntity = characterInventoryRepo
                .findCharacterInventoryEntityByCharacter_IdAndItem_Name(character.getId(), item.getName());
        if(characterInventoryEntity.isPresent()){
            return true;
        }
        return false;
    }

    private Boolean findIfCharacterHas1_5Epic(Character character){
        String epic1_5Name = character.getCharacterClass().getEpic1_5Name();
        Optional<CharacterInventoryEntity> characterInventoryEntity = characterInventoryRepo
                .findCharacterInventoryEntityByCharacter_IdAndItem_Name(character.getId(), epic1_5Name);
        if(characterInventoryEntity.isPresent()){
            return true;
        }
        if(findIfCharacterHas2_0Epic(character)){
            return true;
        }
        return false;
    }

    private Boolean findIfCharacterHas2_0Epic(Character character){
        Optional<CharacterInventoryEntity> characterInventoryEntity = characterInventoryRepo.findCharacterInventoryEntityByCharacter_IdAndItem_Name(
                character.getId(), character.getCharacterClass().getEpic2_0Name());
        if(characterInventoryEntity.isPresent()){
            return true;
        }
        return false;
    }
}
