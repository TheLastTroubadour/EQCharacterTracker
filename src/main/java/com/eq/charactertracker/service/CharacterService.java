package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.exception.CharacterAlreadyExistsException;
import com.eq.charactertracker.exception.CharacterNotFoundException;
import com.eq.charactertracker.factory.CharacterInformationFactory;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.service.shared.ICharacterInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CharacterService {

    private final ModelMapper modelMapper;
    private final CharacterRepo characterRepo;
    private final UserService userService;
    private final ServerService serverService;
    private final ICharacterInformationService characterInformationService;
    private final CharacterInformationFactory characterInformationFactory;

    public Character createCharacter(Character character) {
        UserEntity userEntity = userService.getAuthenticatedUser();
        ServerEntity server = serverService.findByShortName(character.getServer());
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByUserIdAndNameAndServer(
                userEntity.getId(), character.getName(), server);
        if (characterEntity != null) {
            throw new CharacterAlreadyExistsException(character);
        }
        characterEntity = modelMapper.map(character, CharacterEntity.class);
        characterEntity.setServer(server);
        characterEntity.setUser(userEntity);
        characterEntity = characterRepo.save(characterEntity);
        return modelMapper.map(characterEntity, Character.class);
    }

    public Character getCharacterById(Long characterId) {
        Optional<CharacterEntity> characterEntity = characterRepo.findById(characterId);
        if (characterEntity.isPresent()) {
            return modelMapper.map(characterEntity.get(), Character.class);
        }
        throw new CharacterNotFoundException(characterId);
    }

    public List<Character> getAllCharacters() {
        List<CharacterEntity> characterEntityList = characterRepo.findAll();
        List<Character> characterList = new ArrayList<>();
        characterEntityList.forEach(ce -> characterList.add(modelMapper.map(ce, Character.class)));
        return characterList;
    }

    public List<Character> createCharacters(List<Character> characters) {
        List<Character> characterList = new ArrayList<>();
        for (Character character : characters) {
            try {
                characterList.add(createCharacter(character));
            } catch (CharacterAlreadyExistsException e) {
                log.error(String.format("Unable to create Character: %s in list already exists", character));
            }
        }
        return characterList;
    }

    public List<Character> refreshCharacters(String serverShortName) {
        UserEntity user = userService.getAuthenticatedUser();
        List<Character> characters = serverShortName != null
                ? getCharacterByUserIdAndServer(user.getId(), serverService.findByShortName(serverShortName))
                : getCharactersByUserId(user.getId());
        characterInformationService.updateExternalArmorDetailsForAllCharacters(characters);
        return characters;
    }

    public List<Character> createCharactersByServerAndGuildName(String serverShortName, String guild) {
        UserEntity user = userService.getAuthenticatedUser();
        ICharacterInformationService service = characterInformationFactory.getInstance(serverShortName);
        return service.createAllCharactersByGuildName(user.getId(), guild);
    }

    public List<Character> getCharactersByUserId(Long userId) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserId(userId);
        return convertEntityListToModelList(characterEntityList);
    }

    private List<Character> getCharacterByUserIdAndServer(Long userId, ServerEntity server) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserIdAndServer(userId, server);
        return convertEntityListToModelList(characterEntityList);
    }

    private List<Character> convertEntityListToModelList(List<CharacterEntity> entities) {
        List<Character> characterList = new ArrayList<>();
        entities.forEach(e -> characterList.add(modelMapper.map(e, Character.class)));
        return characterList;
    }
}
