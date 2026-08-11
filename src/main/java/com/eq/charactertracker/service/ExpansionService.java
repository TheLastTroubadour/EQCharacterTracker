package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.model.quest.ExpansionDTO;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpansionService extends BaseServiceImpl<ExpansionEntity, ExpansionDTO> {

    private final ExpansionRepo expansionRepo;

    public ExpansionService(ModelMapper modelMapper, ExpansionRepo expansionRepo) {
        super(modelMapper, expansionRepo);
        this.expansionRepo = expansionRepo;
    }

    public ExpansionDTO createExpansion(ExpansionEntity expansion) {
        return entityToDTO(expansionRepo.save(expansion));
    }

    public List<ExpansionDTO> getExpansions(ServerType serverType) {
        return expansionRepo.findByServerTypeOrderByReleaseOrderAsc(serverType).stream()
                .map(this::entityToDTO)
                .toList();
    }
}
