package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.model.imports.SpellRuneImportResult;
import com.eq.charactertracker.model.spell.SpellRequest;
import com.eq.charactertracker.model.spell.SpellDTO;
import com.eq.charactertracker.model.spell.SpellRuneImportRequest;
import com.eq.charactertracker.service.SpellService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("spell")
public class SpellController {

    private final SpellService spellService;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<SpellDTO> importSpells(@RequestBody List<SpellRequest> requests) {
        return spellService.importSpells(requests);
    }

    @PostMapping("/import-runes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public SpellRuneImportResult importRunes(@RequestBody SpellRuneImportRequest request) {
        return spellService.importRunes(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public SpellDTO update(@PathVariable Long id, @RequestBody SpellDTO spell) {
        return spellService.update(spell, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SpellDTO> getSpellsByServerTypeAndCharacterClass(
            @RequestParam(name = "serverType", required = false) ServerType serverType,
            @RequestParam(name = "characterClass", required = false) CharacterClass characterClass) {
        return spellService.getSpellsByServerTypeAndCharacterClass(serverType, characterClass);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SpellDTO getById(@PathVariable Long id) {
        return spellService.getById(id);
    }

    @GetMapping("/needs-review")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public List<SpellDTO> getSpellsNeedingReview() {
        return spellService.getSpellsNeedingReview();
    }
}
