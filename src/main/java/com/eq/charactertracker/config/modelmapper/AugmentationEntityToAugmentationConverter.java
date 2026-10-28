package com.eq.charactertracker.config.modelmapper;

import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.model.Augmentation;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class AugmentationEntityToAugmentationConverter implements Converter<AugmentationEntity, Augmentation> {

    @Override
    public Augmentation convert(MappingContext<AugmentationEntity, Augmentation> mappingContext) {
        return new Augmentation(mappingContext.getSource());
    }
}
