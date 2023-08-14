package com.eq.charactertracker.config.modelmapper;

import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.model.Armor;
import org.modelmapper.PropertyMap;

public class ArmorEntityToArmorModelMap extends PropertyMap<ArmorEntity, Armor> {
    @Override
    protected void configure() {
        using(new ArmorEntityToArmorConverter());
    }
}
