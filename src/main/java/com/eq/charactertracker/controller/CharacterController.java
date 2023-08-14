package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.CharacterEquipSlot;
import com.eq.charactertracker.service.CharacterEquipSlotService;
import com.eq.charactertracker.service.CharacterService;
import com.eq.charactertracker.service.THFCharacterInformationService;
import com.eq.charactertracker.service.THFItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("character")
@CrossOrigin(origins = "*")
public class CharacterController {
    private final CharacterService characterService;
    private final THFCharacterInformationService thfCharacterInformationService;
    private final THFItemService thfItemService;
    private final CharacterEquipSlotService equipSlotService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"{userId}"})
    public Character createCharacter(@RequestBody @Valid Character character, @PathVariable Long userId){
        Character createdCharacter = characterService.createCharacter(character, userId);
        return createdCharacter;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("refresh/{userId}")
    public List<Character> refreshCharacters(@PathVariable Long userId, @RequestParam(required = false,value = "server") ServerEnum server){
        List<Character> characterList = characterService.refreshCharacters(userId, server);
        return characterList;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"userId"})
    public List<Character> createCharacters(@RequestBody List<@Valid Character> characters, @PathVariable Long userId) {
        List<Character> characterList = characterService.createCharacters(characters, userId);
        return characterList;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{characterId}")
    public Character getCharacter(@PathVariable Long characterId){
        return characterService.getCharacterById(characterId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Character> getAllCharacters(){
        return characterService.getAllCharacters();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{characterId}/server/{serverName}/equipped-armor")
    public List<CharacterEquipSlot> getCharacterArmor(@PathVariable Long characterId, @PathVariable ServerEnum serverName){
        return equipSlotService.findCharacterEquipSlotEntitiesByCharacterIdAndCharacterServer(characterId, serverName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping({"refresh/{userId}/inventory"})
    public List<Character> refreshInventory(@PathVariable Long userId){
        return thfItemService.updateInventoryInformation(userId);
    }


}
