package com.eq.charactertracker.config.modelmapper;

import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.model.Augmentation;
import org.modelmapper.PropertyMap;

public class AugmentationEntityToAugmentationModelMap extends PropertyMap<AugmentationEntity, Augmentation> {
    @Override
    protected void configure() {
        using(new AugmentationEntityToAugmentationConverter());
    }
}
