package com.eq.charactertracker.config.modelmapper;

import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.model.ArmorDTO;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ArmorEntityToArmorConverter implements Converter<ArmorEntity, ArmorDTO> {

    @Override
    public ArmorDTO convert(MappingContext<ArmorEntity, ArmorDTO> mappingContext) {
        ArmorEntity armorEntity = mappingContext.getSource();
        ArmorDTO armor = new ArmorDTO(armorEntity);
        return armor;
    }
}
