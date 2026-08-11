package com.eq.charactertracker.service.shared;

import com.eq.charactertracker.model.CharacterDTO;

import java.util.List;

public interface ICharacterInformationService {

    List<CharacterDTO> createAllCharactersByGuildName(Long userId, String guildName);
    void updateExternalArmorDetailsForAllCharacters(List<CharacterDTO> characterList);
    void updateExternalArmorDetails(CharacterDTO character);
}
