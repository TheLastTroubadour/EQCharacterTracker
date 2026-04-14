package com.eq.charactertracker.service.shared;

import com.eq.charactertracker.model.Character;

import java.util.List;

public interface ICharacterInformationService {

    List<Character> createAllCharactersByGuildName(Long userId, String guildName);
    void updateExternalArmorDetailsForAllCharacters(List<Character> characterList);
    void updateExternalArmorDetails(Character character);
}
