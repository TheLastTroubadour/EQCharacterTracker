package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.exception.CharacterAlreadyExistsException;
import com.eq.charactertracker.factory.CharacterInformationFactory;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.service.shared.ICharacterInformationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CharacterService extends BaseServiceImpl<CharacterEntity, CharacterDTO> {

    private final CharacterRepo characterRepo;
    private final UserService userService;
    private final ServerService serverService;
    private final ICharacterInformationService characterInformationService;
    private final CharacterInformationFactory characterInformationFactory;

    public CharacterService(ModelMapper modelMapper, CharacterRepo characterRepo, UserService userService,
                             ServerService serverService, ICharacterInformationService characterInformationService,
                             CharacterInformationFactory characterInformationFactory) {
        super(modelMapper, characterRepo);
        this.characterRepo = characterRepo;
        this.userService = userService;
        this.serverService = serverService;
        this.characterInformationService = characterInformationService;
        this.characterInformationFactory = characterInformationFactory;
    }

    public CharacterDTO createCharacter(CharacterDTO character) {
        UserEntity userEntity = userService.getAuthenticatedUser();
        ServerEntity server = serverService.findByShortName(character.getServer());
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByUserIdAndNameAndServer(
                userEntity.getId(), character.getName(), server);
        if (characterEntity != null) {
            throw new CharacterAlreadyExistsException(character);
        }
        characterEntity = dtoToEntity(character);
        characterEntity.setServer(server);
        characterEntity.setUser(userEntity);
        characterEntity = characterRepo.save(characterEntity);
        return entityToDTO(characterEntity);
    }

    public CharacterDTO getCharacterById(Long characterId) {
        return getById(characterId);
    }

    public List<CharacterDTO> getAllCharacters() {
        return characterRepo.findAll().stream().map(this::entityToDTO).toList();
    }

    public List<CharacterDTO> createCharacters(List<CharacterDTO> characters) {
        List<CharacterDTO> characterList = new ArrayList<>();
        for (CharacterDTO character : characters) {
            try {
                characterList.add(createCharacter(character));
            } catch (CharacterAlreadyExistsException e) {
                log.error(String.format("Unable to create Character: %s in list already exists", character));
            }
        }
        return characterList;
    }

    public List<CharacterDTO> refreshCharacters(String serverShortName) {
        UserEntity user = userService.getAuthenticatedUser();
        List<CharacterDTO> characters = serverShortName != null
                ? getCharacterByUserIdAndServer(user.getId(), serverService.findByShortName(serverShortName))
                : getCharactersByUserId(user.getId());
        characterInformationService.updateExternalArmorDetailsForAllCharacters(characters);
        return characters;
    }

    public List<CharacterDTO> createCharactersByServerAndGuildName(String serverShortName, String guild) {
        UserEntity user = userService.getAuthenticatedUser();
        ICharacterInformationService service = characterInformationFactory.getInstance(serverShortName);
        return service.createAllCharactersByGuildName(user.getId(), guild);
    }

    public List<CharacterDTO> getCharactersByUserId(Long userId) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserId(userId);
        return characterEntityList.stream().map(this::entityToDTO).toList();
    }

    private List<CharacterDTO> getCharacterByUserIdAndServer(Long userId, ServerEntity server) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserIdAndServer(userId, server);
        return characterEntityList.stream().map(this::entityToDTO).toList();
    }
}
