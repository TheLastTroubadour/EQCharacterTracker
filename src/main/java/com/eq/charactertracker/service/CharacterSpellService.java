package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.entity.spell.SpellEntity;
import com.eq.charactertracker.model.spell.CharacterSpellResponse;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.spell.CharacterSpellRepo;
import com.eq.charactertracker.repo.spell.SpellRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterSpellService {

    private final CharacterSpellRepo characterSpellRepo;
    private final SpellRepo spellRepo;
    private final CharacterRepo characterRepo;

    public List<CharacterSpellResponse> getSpellsForCharacter(Long characterId) {
        CharacterEntity character = characterRepo.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        List<SpellEntity> catalog = spellRepo.findByServerTypeAndCharacterClass(
                character.getServer().getType(), character.getCharacterClass());

        Map<Long, CharacterSpellEntity> progressBySpellId = characterSpellRepo.findByCharacter_Id(characterId)
                .stream()
                .collect(Collectors.toMap(cs -> cs.getSpell().getId(), cs -> cs));

        return catalog.stream()
                .map(spell -> toResponse(spell, progressBySpellId.get(spell.getId())))
                .toList();
    }

    @Transactional
    public CharacterSpellResponse toggleScribed(Long characterId, Long spellId) {
        CharacterSpellEntity progress = characterSpellRepo.findByCharacter_IdAndSpell_Id(characterId, spellId)
                .orElseGet(() -> {
                    CharacterEntity character = characterRepo.findById(characterId)
                            .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));
                    SpellEntity spell = spellRepo.findById(spellId)
                            .orElseThrow(() -> new IllegalArgumentException("Spell not found: " + spellId));

                    CharacterSpellEntity created = new CharacterSpellEntity();
                    created.setCharacter(character);
                    created.setSpell(spell);
                    created.setScribed(false);
                    return created;
                });

        progress.setScribed(!progress.isScribed());
        progress = characterSpellRepo.save(progress);
        return toResponse(progress.getSpell(), progress);
    }

    private CharacterSpellResponse toResponse(SpellEntity spell, CharacterSpellEntity progress) {
        CharacterSpellResponse response = new CharacterSpellResponse();
        response.setSpellId(spell.getId());
        response.setCharacterSpellId(progress != null ? progress.getId() : null);
        response.setName(spell.getName());
        response.setRank(spell.getRank());
        response.setLevel(spell.getLevel());
        response.setManaCost(spell.getManaCost());
        response.setDescription(spell.getDescription());
        response.setScribed(progress != null && progress.isScribed());
        return response;
    }
}
