package com.eq.charactertracker.service.shared;

import com.eq.charactertracker.model.Character;

import java.util.List;

public interface ICharacterInformationService {

    void updateExternalArmorDetailsForAllCharacters(List<Character> characterList);
    void updateExternalArmorDetails(Character character);
}
