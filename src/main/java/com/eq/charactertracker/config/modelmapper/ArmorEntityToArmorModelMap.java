package com.eq.charactertracker.config.modelmapper;

import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.model.ArmorDTO;
import org.modelmapper.PropertyMap;

public class ArmorEntityToArmorModelMap extends PropertyMap<ArmorEntity, ArmorDTO> {
    @Override
    protected void configure() {
        using(new ArmorEntityToArmorConverter());
    }
}
