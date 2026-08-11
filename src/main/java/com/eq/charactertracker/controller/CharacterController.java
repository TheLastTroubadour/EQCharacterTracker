package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.CharacterEquipSlotDTO;
import com.eq.charactertracker.service.CharacterEquipSlotService;
import com.eq.charactertracker.service.CharacterService;
import com.eq.charactertracker.service.THFItemService;
import com.eq.charactertracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("character")
public class CharacterController {

    private final CharacterService characterService;
    private final THFItemService thfItemService;
    private final CharacterEquipSlotService equipSlotService;
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CharacterDTO createCharacter(@RequestBody @Valid CharacterDTO character) {
        return characterService.createCharacter(character);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("batch")
    public List<CharacterDTO> createCharacters(@RequestBody List<@Valid CharacterDTO> characters) {
        return characterService.createCharacters(characters);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("refresh")
    public List<CharacterDTO> refreshCharacters(@RequestParam(name = "server", required = false) String server) {
        return characterService.refreshCharacters(server);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("server/{server}/guild/{guild}")
    public List<CharacterDTO> createCharactersByServerAndGuildName(
            @PathVariable String server,
            @PathVariable String guild) {
        return characterService.createCharactersByServerAndGuildName(server, guild);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{characterId}")
    public CharacterDTO getCharacter(@PathVariable Long characterId) {
        return characterService.getCharacterById(characterId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CharacterDTO> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{characterId}/server/{serverName}/equipped-armor")
    public List<CharacterEquipSlotDTO> getCharacterArmor(@PathVariable Long characterId,
                                                       @PathVariable String serverName) {
        return equipSlotService.findCharacterEquipSlotEntitiesByCharacterIdAndCharacterServer(characterId, serverName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("refresh/inventory")
    public List<CharacterDTO> refreshInventory() {
        return thfItemService.updateInventoryInformation(userService.getAuthenticatedUser().getId());
    }
}
