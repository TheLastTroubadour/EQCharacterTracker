package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.constants.Tier5PoAAugmentItemsEnum;
import com.eq.charactertracker.constants.Tier5XegonyKeyQuestItemsEnum;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.quest.thf.QuestItemEntity;
import com.eq.charactertracker.entity.quest.thf.Tier5PoAAugmentQuestEntity;
import com.eq.charactertracker.entity.quest.thf.Tier5XegonyKeyQuestEntity;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.quest.thf.Tier5PoAAugmentQuest;
import com.eq.charactertracker.model.quest.thf.Tier5XegonyKeyQuest;
import com.eq.charactertracker.repo.CharacterInventoryRepo;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.QuestItemRepo;
import com.eq.charactertracker.repo.Tier5PoAAugmentQuestRepo;
import com.eq.charactertracker.repo.Tier5XegonyKeyQuestRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Tier5QuestService {
    private final Tier5XegonyKeyQuestRepo tier5XegonyKeyQuestRepo;
    private final ModelMapper modelMapper;
    private final CharacterRepo characterRepo;
    private final CharacterService characterService;
    private final QuestService questService;
    private final Tier5PoAAugmentQuestRepo tier5PoAAugmentQuestRepo;
    private final CharacterInventoryRepo characterInventoryRepo;
    private final THFItemService thfItemService;
    private final QuestItemRepo questItemRepo;

    private static final Long WIND_PARCHMENT_EXTERNAL_ID = 13086L;
    private static final Long CONTAINER_OF_MIST_EXTERNAL_ID = 13228L;
    public List<Tier5XegonyKeyQuest> getAllTier5XegonyQuests() {
        List<Tier5XegonyKeyQuestEntity> tier5XegonyKeyQuestEntities = tier5XegonyKeyQuestRepo.findAll();
        List<Tier5XegonyKeyQuest> tier5XegonyKeyQuests = new ArrayList<>();
        tier5XegonyKeyQuestEntities.forEach(e -> tier5XegonyKeyQuests.add(modelMapper.map(e, Tier5XegonyKeyQuest.class)));
        return tier5XegonyKeyQuests;
    }

    public List<Tier5PoAAugmentQuest> getAllTier5PoAAugmentQuests(){
        List<Tier5PoAAugmentQuest> list = new ArrayList<>();
        List<Tier5PoAAugmentQuestEntity> entityList = tier5PoAAugmentQuestRepo.findAll();
        entityList.forEach(e -> list.add(modelMapper.map(e, Tier5PoAAugmentQuest.class)));
        return list;
    }

    //TODO make an object that returns a list of all quests
    public List<Tier5XegonyKeyQuest> updateTier5Quests(){
        List<Character> characterList = characterService.getAllCharacters();
        thfItemService.updateInventoryInformation(characterList);

        updateTier5PoAAugmentQuests(characterList);
        return updateTier5XegonyKeyQuests(characterList);
    }

    private List<Tier5PoAAugmentQuest> updateTier5PoAAugmentQuests(List<Character> characterList){
        List<Tier5PoAAugmentQuest> returnList = new ArrayList<>();
        for(Character c: characterList){
            CharacterEntity characterEntity = characterRepo.findCharacterEntityByNameAndServer(c.getName(), c.getServer());
            Arrays.stream(Tier5PoAAugmentItemsEnum.values()).forEach(v ->
                    {
                        Optional<CharacterInventoryEntity> characterInventoryEntity = characterInventoryRepo
                                .findCharacterInventoryEntityByCharacter_IdAndItem_ExternalIdAndItem_Server
                                        (c.getId(), v.getExternalId(), ServerEnum.THF);
                        if(characterInventoryEntity.isPresent()){
                            Optional<Tier5PoAAugmentQuestEntity> entity = tier5PoAAugmentQuestRepo
                                    .findTier5PoAAugmentQuestEntityByCharacter_IdAndQuestItem_Item_ExternalId(c.getId(), v.getExternalId());
                            Tier5PoAAugmentQuestEntity tier5PoAAugmentQuestEntity;
                            if(entity.isPresent()){
                                tier5PoAAugmentQuestEntity = entity.get();
                            }else{
                                tier5PoAAugmentQuestEntity = new Tier5PoAAugmentQuestEntity();
                                tier5PoAAugmentQuestEntity.setCharacter(characterEntity);
                                tier5PoAAugmentQuestEntity.setQuestItem(createPoABaseQuestItem(v));
                                tier5PoAAugmentQuestEntity.setWindParchment(createWindParchmentQuestItem());
                                tier5PoAAugmentQuestEntity.setContainerOfMist(createContainerOfMistQuestItem());
                            }

                            questService.setAllQuestItemEntityFields(characterEntity, tier5PoAAugmentQuestEntity);

                            tier5PoAAugmentQuestRepo.save(tier5PoAAugmentQuestEntity);
                            returnList.add(modelMapper.map(tier5PoAAugmentQuestEntity, Tier5PoAAugmentQuest.class));
                        }

                    }
            );
        }
        return returnList;
    }

    private QuestItemEntity createContainerOfMistQuestItem() {
        QuestItemEntity questItemEntity = new QuestItemEntity();
        ItemEntity item = thfItemService.createOrGetItemEntity(CONTAINER_OF_MIST_EXTERNAL_ID, ServerEnum.THF);
        questItemEntity.setItem(item);
        questItemRepo.save(questItemEntity);
        return questItemEntity;
    }

    private QuestItemEntity createWindParchmentQuestItem() {
        QuestItemEntity questItemEntity = new QuestItemEntity();
        ItemEntity item = thfItemService.createOrGetItemEntity(WIND_PARCHMENT_EXTERNAL_ID, ServerEnum.THF);
        questItemEntity.setItem(item);
        questItemRepo.save(questItemEntity);
        return questItemEntity;
    }

    private QuestItemEntity createPoABaseQuestItem(Tier5PoAAugmentItemsEnum v) {
        QuestItemEntity questItemEntity = new QuestItemEntity();
        ItemEntity item = thfItemService.createOrGetItemEntity(v.getExternalId(), ServerEnum.THF);
        questItemEntity.setItem(item);
        questItemRepo.save(questItemEntity);
        return questItemEntity;
    }

    private List<Tier5XegonyKeyQuest> updateTier5XegonyKeyQuests(List<Character> characterList){
        List<Tier5XegonyKeyQuest> tier5XegonyKeyQuestList = new ArrayList<>();
        for (Character c : characterList) {
            CharacterEntity characterEntity = characterRepo.findCharacterEntityByNameAndServer(c.getName(), c.getServer());
            Optional<Tier5XegonyKeyQuestEntity> tier5XegonyKeyQuestEntity = tier5XegonyKeyQuestRepo.findTier5XegonyKeyQuestEntityByCharacterId(c.getId());
            Tier5XegonyKeyQuestEntity entity;
            if(tier5XegonyKeyQuestEntity.isPresent()){
                entity = tier5XegonyKeyQuestEntity.get();
            }else{
                entity = new Tier5XegonyKeyQuestEntity();
                entity.setCharacter(characterEntity);

                QuestItemEntity mysticalEssenceOfDust = new QuestItemEntity();
                mysticalEssenceOfDust.setItem(thfItemService.createOrGetItemEntity(Tier5XegonyKeyQuestItemsEnum.MYSTICAL_ESSENCE_OF_DUST.getExternalId(), ServerEnum.THF));
                entity.setMysticalEssenceOfDust(mysticalEssenceOfDust);

                QuestItemEntity mysticalEssenceOfMist= new QuestItemEntity();
                mysticalEssenceOfMist.setItem(thfItemService.createOrGetItemEntity(Tier5XegonyKeyQuestItemsEnum.MYSTICAL_ESSENCE_OF_MIST.getExternalId(), ServerEnum.THF));
                entity.setMysticalEssenceOfMist(mysticalEssenceOfMist);

                QuestItemEntity mysticalEssenceOfWind = new QuestItemEntity();
                mysticalEssenceOfWind.setItem(thfItemService.createOrGetItemEntity(Tier5XegonyKeyQuestItemsEnum.MYSTICAL_ESSENCE_OF_WIND.getExternalId(), ServerEnum.THF));
                entity.setMysticalEssenceOfWind(mysticalEssenceOfWind);

                QuestItemEntity mysticalEssenceOfSmoke= new QuestItemEntity();
                mysticalEssenceOfSmoke.setItem(thfItemService.createOrGetItemEntity(Tier5XegonyKeyQuestItemsEnum.MYSTICAL_ESSENCE_OF_SMOKE.getExternalId(), ServerEnum.THF));
                entity.setMysticalEssenceOfSmoke(mysticalEssenceOfSmoke);
            }

            questService.setAllQuestItemEntityFields(characterEntity, entity, false);
            if(entity.getMysticalEssenceOfDust().getInInventory() &&
                    entity.getMysticalEssenceOfSmoke().getInInventory() &&
                    entity.getMysticalEssenceOfMist().getInInventory() &&
                    entity.getMysticalEssenceOfWind().getInInventory()) {
                entity.setCompleted(true);
            }
            tier5XegonyKeyQuestRepo.save(entity);
            tier5XegonyKeyQuestList.add(modelMapper.map(entity, Tier5XegonyKeyQuest.class));
        }
        return tier5XegonyKeyQuestList;
    }

}
