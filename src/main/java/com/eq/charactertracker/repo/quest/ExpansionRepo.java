package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpansionRepo extends JpaRepository<ExpansionEntity, Long> {
    Optional<ExpansionEntity> findByShortNameAndServerType(String shortName, ServerType serverType);
    List<ExpansionEntity> findByServerTypeOrderByReleaseOrderAsc(ServerType serverType);
}
