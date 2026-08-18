package com.eq.charactertracker.repo.quest;

import com.eq.charactertracker.entity.quest.RequirementProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementProgressRepo extends JpaRepository<RequirementProgressEntity, Long> {
    void deleteByRequirementTemplate_Id(Long requirementTemplateId);
}
