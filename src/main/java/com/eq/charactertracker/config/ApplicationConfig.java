package com.eq.charactertracker.config;

import com.eq.charactertracker.config.modelmapper.ArmorEntityToArmorModelMap;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.Item;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addMappings(new ArmorEntityToArmorModelMap());

        modelMapper.typeMap(CharacterEntity.class, CharacterDTO.class)
                .addMappings(m -> m.map(
                        src -> src.getServer().getShortName(),
                        CharacterDTO::setServer));

        modelMapper.typeMap(ItemEntity.class, Item.class)
                .addMappings(m -> m.map(
                        src -> src.getServer().getShortName(),
                        Item::setServer));

        return modelMapper;
    }
}
