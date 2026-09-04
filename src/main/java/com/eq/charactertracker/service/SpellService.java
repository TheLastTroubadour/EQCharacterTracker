package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.constants.spell.SpellUnlockType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.entity.spell.SpellEntity;
import com.eq.charactertracker.model.spell.SpellRequest;
import com.eq.charactertracker.model.spell.SpellDTO;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import com.eq.charactertracker.repo.spell.SpellRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpellService extends BaseServiceImpl<SpellEntity, SpellDTO> {

    private final SpellRepo spellRepo;
    // TODO: route through ExpansionService instead of calling ExpansionRepo directly
    private final ExpansionRepo expansionRepo;

    public SpellService(ModelMapper modelMapper, SpellRepo spellRepo, ExpansionRepo expansionRepo) {
        super(modelMapper, spellRepo);
        this.spellRepo = spellRepo;
        this.expansionRepo = expansionRepo;
    }

    @Transactional
    public SpellEntity importSpell(SpellRequest request) {
        SpellEntity spell = spellRepo
                .findByNameAndRankAndCharacterClassAndServerType(request.getName(), request.getRank(), request.getCharacterClass(), request.getServerType())
                .orElse(new SpellEntity());

        spell.setName(request.getName());
        spell.setRank(request.getRank());
        spell.setCharacterClass(request.getCharacterClass());
        spell.setServerType(request.getServerType());
        spell.setLevel(request.getLevel());
        spell.setManaCost(request.getManaCost());
        spell.setEnduranceCost(request.getEnduranceCost());
        spell.setDescription(request.getDescription());
        spell.setUnlockType(request.getUnlockType() != null ? request.getUnlockType() : SpellUnlockType.NONE);
        spell.setRuneName(request.getRuneName());
        spell.setRuneTurnInOrder(request.getRuneTurnInOrder());

        if (request.getExpansionShortName() != null) {
            ExpansionEntity expansion = expansionRepo
                    .findByShortNameAndServerType(request.getExpansionShortName(), request.getServerType())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Expansion not found: " + request.getExpansionShortName()));
            spell.setExpansion(expansion);
        }

        return spellRepo.save(spell);
    }

    @Transactional
    public List<SpellDTO> importSpells(List<SpellRequest> requests) {
        return requests.stream().map(this::importSpell).map(this::entityToDTO).toList();
    }

    public List<SpellDTO> getSpellsByServerTypeAndCharacterClass(ServerType serverType, CharacterClass characterClass) {
        List<SpellEntity> spells;
        if (serverType != null && characterClass != null) {
            spells = spellRepo.findByServerTypeAndCharacterClass(serverType, characterClass);
        } else if (serverType != null) {
            spells = spellRepo.findByServerType(serverType);
        } else {
            spells = spellRepo.findAll();
        }
        return spells.stream().map(this::entityToDTO).toList();
    }
}

