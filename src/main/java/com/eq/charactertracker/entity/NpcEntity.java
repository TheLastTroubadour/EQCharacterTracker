package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.NpcTier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Table(name = "npcs")
@Getter
@Setter
@NoArgsConstructor
public class NpcEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Not unique -- the same name can exist as a genuinely different creature in a different
    // zone (e.g. Deathfang in Chardok vs. Deathfang in The Steppes). Uniqueness only holds
    // per (name, zone), never globally.
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NpcTier tier = NpcTier.GROUP_NAMED;

    // LIVE's optional loot-randomization ruleset: NPCs sharing the same tier number all draw
    // from the same shared item pool (see ArmorEntity.randomLootTier), rather than a fixed
    // per-NPC drop table. Not server-scoped -- same tier numbering wherever it's enabled.
    private Integer randomLootTier;

    @ManyToMany
    @JoinTable(
            name = "npc_zones",
            joinColumns = @JoinColumn(name = "npc_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private Collection<ZoneEntity> zones;

    // Populated manually via a future UI, not from any import -- no reliable automated
    // drop-source data yet (Hunter export doesn't carry loot info).
    @ManyToMany
    @JoinTable(
            name = "npc_armor",
            joinColumns = @JoinColumn(name = "npc_id"),
            inverseJoinColumns = @JoinColumn(name = "armor_id")
    )
    private Collection<ArmorEntity> drops;
}
