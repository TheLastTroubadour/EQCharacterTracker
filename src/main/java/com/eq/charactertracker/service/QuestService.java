package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.quest.thf.QuestItemEntity;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.QuestItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestService {

    private final ModelMapper modelMapper;
    private final CharacterItemInventoryService characterItemInventoryService;

    public void setAllQuestItemEntityFields(CharacterEntity character, Object source) {
        setAllQuestItemEntityFields(character, source, false);
    }

    public void setAllQuestItemEntityFields(CharacterEntity character, Object source, boolean isCompleted) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object obj = f.get(source);
                if(obj instanceof QuestItemEntity){
                    if(obj == null){
                        //Find and create object
                    }

                }
                //need to create here
                if (obj != null && obj instanceof QuestItemEntity) {
                    QuestItemEntity questItemEntity = (QuestItemEntity) obj;
                    if (isCompleted) {
                        questItemEntity.setInInventory(true);
                    } else {
                        questItemEntity.setInInventory(characterItemInventoryService.findIfCharacterHasItemInInventory(modelMapper.map(character, Character.class), modelMapper.map(questItemEntity, QuestItem.class)));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(String.format("Unable to access field on %s with field name %s on character %s", f, f.getName(), character.getName()));
            }
            f.setAccessible(false);
        }
    }


}
