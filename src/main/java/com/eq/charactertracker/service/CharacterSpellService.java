package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.spell.CharacterSpellDTO;
import com.eq.charactertracker.model.spell.SpellDTO;
import com.eq.charactertracker.repo.spell.CharacterSpellRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CharacterSpellService extends BaseServiceImpl<CharacterSpellEntity, CharacterSpellDTO> {

    private final SpellService spellService;
    private final CharacterSpellRepo characterSpellRepo;

    public CharacterSpellService(ModelMapper modelMapper, SpellService spellService, CharacterSpellRepo characterSpellRepo) {
        super(modelMapper, characterSpellRepo);
        this.spellService = spellService;
        this.characterSpellRepo = characterSpellRepo;
    }

    public List<CharacterSpellDTO> getSpellsForCharacter(Long characterId) {
        List<CharacterSpellEntity> progressBySpellId = characterSpellRepo.findByCharacter_Id(characterId);

        return progressBySpellId.stream().map(this::entityToDTO).toList();
    }

    @Transactional
    public void seedSpellsForCharacter(CharacterDTO character) {
        List<SpellDTO> catalog = spellService.getSpellsByServerTypeAndCharacterClass(character.getServer().getType(), character.getCharacterClass());

        Set<Long> existingSpellIds = characterSpellRepo.findByCharacter_Id(character.getId()).stream()
                .map(characterSpell -> characterSpell.getSpell().getId())
                .collect(Collectors.toSet());

        List<CharacterSpellDTO> missing = catalog.stream()
                .filter(spell -> !existingSpellIds.contains(spell.getId()))
                .map(spell -> {

                    CharacterSpellDTO characterSpell = new CharacterSpellDTO();
                    characterSpell.setCharacter(character);
                    characterSpell.setSpell(spell);
                    characterSpell.setScribed(false);
                    return characterSpell;
                }).toList();

        saveAll(missing);
    }

    public List<CharacterSpellDTO> getMissingSpells(Long userId, Long characterId, String server, CharacterClass characterClass) {
        List<CharacterSpellEntity> entities = characterSpellRepo.findMissingSpells(userId, characterId, server, characterClass);
        return entities.stream().map(this::entityToDTO).toList();
    }
}
