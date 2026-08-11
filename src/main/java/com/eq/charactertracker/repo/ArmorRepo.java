package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArmorRepo extends BaseRepo<ArmorEntity, Long> {

    Optional<ArmorEntity> findArmorEntityByExtIdAndServer(Long extId, ServerEntity server);
}
