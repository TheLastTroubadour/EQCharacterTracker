package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.spell.CharacterSpellResponse;
import com.eq.charactertracker.service.CharacterSpellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<CharacterSpellResponse> getSpellsForCharacter(@PathVariable Long characterId) {
        return characterSpellService.getSpellsForCharacter(characterId);
    }

    @PatchMapping("/toggle")
    @ResponseStatus(HttpStatus.OK)
    public CharacterSpellResponse toggleScribed(
            @RequestParam("characterId") Long characterId,
            @RequestParam("spellId") Long spellId) {
        return characterSpellService.toggleScribed(characterId, spellId);
    }
}
