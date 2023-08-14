package com.eq.charactertracker.util;

import com.eq.charactertracker.model.Armor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class CharacterArmorUtil {
    public static void setNullFieldsToZero(Armor armor) {
        Field[] superClassFields = armor.getClass().getSuperclass().getDeclaredFields();

        for(Field field: superClassFields){
            try {
                field.setAccessible(true);
                Object obj = field.get(armor);
                if(obj == null){
                    if(field.getType().equals(Integer.class)) {
                        field.set(armor, 0);
                    } else if (field.getType().equals(Double.class)) {
                        field.set(armor, Double.valueOf("0"));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(String.format("Can't set field %s Illegal access Exception %s", field.getName(), e.getMessage()));
            }finally {
                field.setAccessible(false);
            }
        }
    }
}
