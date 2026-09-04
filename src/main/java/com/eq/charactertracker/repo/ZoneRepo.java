package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.ZoneEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepo extends BaseRepo<ZoneEntity, Long> {
    Optional<ZoneEntity> findByNameIgnoreCase(String name);

    // Name alone isn't unique -- a revamped zone can reuse the same name in a later
    // expansion (e.g. "The Temple of Veeshan" in Velious vs. Claws of Veeshan), so lookups
    // that care about "is this the same zone" need to scope by expansion too.
    Optional<ZoneEntity> findByNameIgnoreCaseAndExpansion_Id(String name, Long expansionId);

    Optional<ZoneEntity> findByNameIgnoreCaseAndExpansionIsNull(String name);

    List<ZoneEntity> findByNameContainingIgnoreCase(String name);
}
