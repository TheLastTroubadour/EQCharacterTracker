package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.AugmentationEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AugmentationRepo extends JpaRepository<AugmentationEntity, Long> {
    Optional<AugmentationEntity> findByExtIdAndServer(Long extId, ServerEntity server);
}
