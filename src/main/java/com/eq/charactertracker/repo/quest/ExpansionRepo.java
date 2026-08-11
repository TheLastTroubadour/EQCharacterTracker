package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.repo.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpansionRepo extends BaseRepo<ExpansionEntity, Long> {
    Optional<ExpansionEntity> findByShortNameAndServerType(String shortName, ServerType serverType);
    List<ExpansionEntity> findByServerTypeOrderByReleaseOrderAsc(ServerType serverType);
}
