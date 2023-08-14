package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.exception.CharacterAlreadyExistsException;
import com.eq.charactertracker.exception.CharacterNotFoundException;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.UserRepo;
import com.eq.charactertracker.service.shared.ICharacterInformationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CharacterRepo characterRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ICharacterInformationService characterInformationService;

    public Character createCharacter(Character character, Long userId) {
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByUserIdAndNameAndServer(userId, character.getName(), character.getServer());
        if(characterEntity != null){
            throw new CharacterAlreadyExistsException(character);
        }
        characterEntity = modelMapper.map(character, CharacterEntity.class);
        UserEntity userEntity = userRepo.getReferenceById(userId);
        characterEntity.setUser(userEntity);
        characterEntity = characterRepo.save(characterEntity);

        Character returnedCharacter = modelMapper.map(characterEntity, Character.class);


        return returnedCharacter;
    }

    public Character getCharacterById(Long characterId) {

        Optional<CharacterEntity> characterEntity = characterRepo.findById(characterId);
        if(characterEntity.isPresent()) {
            Character character = modelMapper.map(characterEntity.get(), Character.class);;
            return character;
        }
        throw new CharacterNotFoundException(characterId);
    }

    public List<Character> getAllCharacters() {
        List<CharacterEntity> characterEntityList = characterRepo.findAll();
        List<Character> characterList = new ArrayList<>();
        characterEntityList.stream().forEach(ce -> characterList.add(modelMapper.map(ce, Character.class)));
        return characterList;
    }

    public List<Character> createCharacters(List<Character> characters, Long userId) {
        List<Character> characterList = new ArrayList<>();
        for(Character character: characters){
            characterList.add(createCharacter(character, userId));
        }
        return characterList;
    }

    public List<Character> searchCharacters(Pageable pageable){
        Page<CharacterEntity> characterEntityList = characterRepo.findAll(pageable);
        List<Character> characterList = new ArrayList<>();
        for(CharacterEntity characterEntity: characterEntityList){
            characterList.add(modelMapper.map(characterEntity, Character.class));
        }
        return characterList;
    }

    public List<Character> getCharactersByUserId(Long userId){
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserId(userId);
        return convertCharacterEntityListToCharacterModelList(characterEntityList);
    }

    public List<Character> refreshCharacters(Long userId, ServerEnum serverEnum) {
        List<Character> characters;
        if(serverEnum != null){
            characters = getCharacterByUserIdAndServer(userId, serverEnum);
        }else {
            characters = getCharactersByUserId(userId);
        }
        characterInformationService.updateExternalArmorDetailsForAllCharacters(characters);
        return characters;
    }

    private List<Character> getCharacterByUserIdAndServer(Long userId, ServerEnum serverEnum) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserIdAndServer(userId, serverEnum);
        return convertCharacterEntityListToCharacterModelList(characterEntityList);
    }

    private List<Character> convertCharacterEntityListToCharacterModelList(List<CharacterEntity> entities){
        List<Character> characterList = new ArrayList<>();
        entities.stream().forEach(e -> characterList.add(modelMapper.map(e, Character.class)));
        return characterList;
    }
}
