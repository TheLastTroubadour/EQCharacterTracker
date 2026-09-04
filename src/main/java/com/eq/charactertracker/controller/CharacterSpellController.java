package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.model.spell.CharacterSpellDTO;
import com.eq.charactertracker.service.CharacterSpellService;
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
@RequestMapping("character-spell")
public class CharacterSpellController {

    private final CharacterSpellService characterSpellService;

    @GetMapping("/character/{characterId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CharacterSpellDTO> getSpellsForCharacter(@PathVariable Long characterId) {
        return characterSpellService.getSpellsForCharacter(characterId);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CharacterSpellDTO update(
            @PathVariable Long id,
            @RequestBody CharacterSpellDTO characterSpellDTO) {
        return characterSpellService.update(characterSpellDTO, id);
    }

    @GetMapping("/missing-spells")
    @ResponseStatus(HttpStatus.OK)
    public List<CharacterSpellDTO> getMissingSpells(
            @RequestParam Long userId,
            @RequestParam(required = false) Long characterId,
            @RequestParam(required = false) String server,
            @RequestParam(required = false) CharacterClass characterClass
    ) {
        return characterSpellService.getMissingSpells(userId, characterId, server, characterClass);
    }
}
