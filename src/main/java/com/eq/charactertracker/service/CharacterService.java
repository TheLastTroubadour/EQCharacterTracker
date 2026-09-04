package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.exception.CharacterAlreadyExistsException;
import com.eq.charactertracker.factory.CharacterInformationFactory;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.model.UserDTO;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.service.shared.ICharacterInformationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
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
    private final CharacterSpellService characterSpellService;
    private final QuestProgressService questProgressService;

    public CharacterService(ModelMapper modelMapper, CharacterRepo characterRepo, UserService userService,
                             ServerService serverService, ICharacterInformationService characterInformationService,
                             CharacterInformationFactory characterInformationFactory,
                             CharacterSpellService characterSpellService, @Lazy QuestProgressService questProgressService) {
        super(modelMapper, characterRepo);
        this.characterRepo = characterRepo;
        this.userService = userService;
        this.serverService = serverService;
        this.characterInformationService = characterInformationService;
        this.characterInformationFactory = characterInformationFactory;
        this.characterSpellService = characterSpellService;
        this.questProgressService = questProgressService;
    }

    public CharacterDTO createCharacter(CharacterDTO character) {
        UserDTO user = userService.getAuthenticatedUser();
        ServerDTO server = serverService.findByName(character.getServer().getName());
        CharacterDTO check = findCharacterByUserIdAndNameAndServer(user.getId(), character.getName(), server);
        if (check != null) {
            throw new CharacterAlreadyExistsException(character);
        }

        character.setServer(server);
        character.setUser(user);
        character = create(character);
        characterSpellService.seedSpellsForCharacter(character);
        questProgressService.seedQuestsForCharacter(character);
        return character;
    }

    public CharacterDTO findCharacterByUserIdAndNameAndServer(Long userId, String name, ServerDTO server){
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByUserIdAndNameAndServer(
                userId, name, getModelMapper().map(server, ServerEntity.class));
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
        UserDTO user = userService.getAuthenticatedUser();
        List<CharacterDTO> characters = serverShortName != null
                ? getCharacterByUserIdAndServer(user.getId(), serverService.findByName(serverShortName))
                : getCharactersByUserId(user.getId());
        characterInformationService.updateExternalArmorDetailsForAllCharacters(characters);
        return characters;
    }

    public List<CharacterDTO> createCharactersByServerAndGuildName(String serverShortName, String guild) {
        UserDTO user = userService.getAuthenticatedUser();
        ICharacterInformationService service = characterInformationFactory.getInstance(serverShortName);
        return service.createAllCharactersByGuildName(user.getId(), guild);
    }

    public List<CharacterDTO> getCharactersByUserId(Long userId) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserId(userId);
        return characterEntityList.stream().map(this::entityToDTO).toList();
    }

    private List<CharacterDTO> getCharacterByUserIdAndServer(Long userId, ServerDTO server) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserIdAndServer(userId, getModelMapper().map(server, ServerEntity.class));
        return characterEntityList.stream().map(this::entityToDTO).toList();
    }
}
