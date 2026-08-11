package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.spell.CharacterSpellEntity;
import com.eq.charactertracker.model.spell.CharacterSpellDTO;
import com.eq.charactertracker.repo.spell.CharacterSpellRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: rename CharacterSpellResponse to CharacterSpellDTO and consider wiring to BaseServiceImpl pattern
@Service
public class CharacterSpellService extends BaseServiceImpl<CharacterSpellEntity, CharacterSpellDTO> {

    private final CharacterSpellRepo characterSpellRepo;

    public CharacterSpellService(ModelMapper modelMapper, CharacterSpellRepo characterSpellRepo) {
        super(modelMapper, characterSpellRepo);
        this.characterSpellRepo = characterSpellRepo;
    }

    public List<CharacterSpellDTO> getSpellsForCharacter(Long characterId) {
        List<CharacterSpellEntity> progressBySpellId = characterSpellRepo.findByCharacter_Id(characterId);

        return progressBySpellId.stream().map(this::entityToDTO).toList();
    }

}
