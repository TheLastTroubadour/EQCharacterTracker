package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.entity.spell.SpellEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.model.spell.CharacterSpellDTO;
import com.eq.charactertracker.repo.spell.CharacterSpellRepo;
import com.eq.charactertracker.repo.spell.SpellRepo;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CharacterSpellService extends BaseServiceImpl<CharacterSpellEntity, CharacterSpellDTO> {

    private final CharacterService characterService;
    private final CharacterSpellRepo characterSpellRepo;
    private final SpellRepo spellRepo;

    public CharacterSpellService(ModelMapper modelMapper, @Lazy CharacterService characterService, CharacterSpellRepo characterSpellRepo, SpellRepo spellRepo) {
        super(modelMapper, characterSpellRepo);
        this.characterService = characterService;
        this.characterSpellRepo = characterSpellRepo;
        this.spellRepo = spellRepo;
    }

    public List<CharacterSpellDTO> getSpellsForCharacter(Long characterId) {
        List<CharacterSpellEntity> progressBySpellId = characterSpellRepo.findByCharacter_Id(characterId);

        return progressBySpellId.stream().map(this::entityToDTO).toList();
    }

    @Transactional
    public void seedSpellsForCharacter(CharacterEntity character) {
        List<SpellEntity> catalog = spellRepo.findByServerTypeAndCharacterClass(
                character.getServer().getType(), character.getCharacterClass());

        Set<Long> existingSpellIds = characterSpellRepo.findByCharacter_Id(character.getId()).stream()
                .map(characterSpell -> characterSpell.getSpell().getId())
                .collect(Collectors.toSet());

        List<CharacterSpellEntity> missing = catalog.stream()
                .filter(spell -> !existingSpellIds.contains(spell.getId()))
                .map(spell -> {
                    CharacterSpellEntity characterSpell = new CharacterSpellEntity();
                    characterSpell.setCharacter(character);
                    characterSpell.setSpell(spell);
                    characterSpell.setScribed(false);
                    return characterSpell;
                }).toList();

        characterSpellRepo.saveAll(missing);
    }

    public List<CharacterSpellDTO> getMissingSpells(Long userId) {
        List<CharacterDTO> characters = characterService.getCharactersByUserId(userId);
        List<CharacterSpellEntity> entities = characterSpellRepo.findByCharacterIdList(characters.stream().map(c -> c.getId()).toList());
        return entities.stream().map(this::entityToDTO).toList();
    }
}
