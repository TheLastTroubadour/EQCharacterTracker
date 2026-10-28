package com.eq.charactertracker.repo;

import com.eq.charactertracker.constants.NpcTier;
import com.eq.charactertracker.entity.NpcEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NpcRepo extends BaseRepo<NpcEntity, Long> {
    Optional<NpcEntity> findByNameIgnoreCase(String name);

    // Name alone isn't unique -- the same name can be a genuinely different creature in a
    // different zone (e.g. Deathfang in Chardok vs. Deathfang in The Steppes), so lookups
    // that care about "is this the same NPC" need to scope by zone too.
    Optional<NpcEntity> findByNameIgnoreCaseAndZones_Id(String name, Long zoneId);

    List<NpcEntity> findByRandomLootTier(Integer randomLootTier);

    // The raid-import review queue -- every npc created from a Raids / "Conqueror of"
    // achievement row lands here until a human confirms or deletes it.
    List<NpcEntity> findByNeedsReviewTrue();

    @Query("SELECT COALESCE(MAX(n.randomLootTier), 0) FROM NpcEntity n")
    Integer findMaxRandomLootTier();

    // LEFT JOIN (not JOIN) so an npc with no zones at all still shows up when zoneId/expansionId
    // aren't being filtered on -- an inner join would silently drop zone-less npcs even for a
    // plain name search. All four filters are optional and independent.
    //
    // namePattern is pre-built in Java ("%needle%", already lower-cased) and bound once --
    // binding the same named parameter both bare (IS NULL check) and nested inside CONCAT()
    // in one query confused MySQL's prepared-statement parameter-type inference and made the
    // whole predicate silently match nothing whenever a null name was passed.
    @Query("SELECT DISTINCT n FROM NpcEntity n LEFT JOIN n.zones z " +
            "WHERE (:namePattern IS NULL OR LOWER(n.name) LIKE :namePattern) " +
            "AND (:zoneId IS NULL OR z.id = :zoneId) " +
            "AND (:expansionId IS NULL OR z.expansion.id = :expansionId) " +
            "AND (:tier IS NULL OR n.tier = :tier)")
    List<NpcEntity> search(@Param("namePattern") String namePattern, @Param("zoneId") Long zoneId,
                            @Param("expansionId") Long expansionId, @Param("tier") NpcTier tier);

}
