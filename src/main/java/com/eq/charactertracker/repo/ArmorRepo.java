package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArmorRepo extends BaseRepo<ArmorEntity, Long> {

    Optional<ArmorEntity> findArmorEntityByExtIdAndServer(Long extId, ServerEntity server);

    List<ArmorEntity> findByServer_Type(ServerType serverType);

    List<ArmorEntity> findByNameContainingIgnoreCase(String name);

    // namePattern is pre-built in Java ("%needle%", already lower-cased) and bound once --
    // see NpcRepo.search for why binding the same param both bare and inside CONCAT() in one
    // query broke MySQL's parameter-type inference.
    @Query("SELECT a FROM ArmorEntity a " +
            "WHERE (:namePattern IS NULL OR LOWER(a.name) LIKE :namePattern) " +
            "AND (:expansionId IS NULL OR a.expansion.id = :expansionId)")
    List<ArmorEntity> search(@Param("namePattern") String namePattern, @Param("expansionId") Long expansionId);
}
