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

    // Set true when this row was auto-created from the Raids / "Conqueror of" side of the
    // Hunter import. That achievement data mixes real raid mobs with encounter/event step
    // names ("Complete Phase 1", "The Roots of Evil"), so every raid-imported npc lands
    // flagged for a manual pass -- confirm it's an actual creature, delete it if it isn't,
    // fill in drops -- and the flag is cleared via PUT /npc/{id}.
    @Column(name = "needs_review", nullable = false)
    private boolean needsReview = false;

    // Distinct from tier -- an encounter is a multi-phase event/objective (e.g. "Complete
    // Phase 1"), not a single creature, but it can still carry a difficulty tier (a raid
    // encounter is still RAID_NAMED) and still drop loot, so it isn't modeled as a NpcTier
    // value. Curated the same way as needsReview: raid-imported rows that turn out to be an
    // event rather than a mob get this flipped true via PUT /npc/{id} instead of being deleted.
    @Column(nullable = false)
    private boolean encounter = false;

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

    // Non-armor loot (weapons, augs, tradeskill/quest items, etc.) -- later expansions'
    // encounters drop plenty of this alongside or instead of stat-bearing armor, so it's
    // tracked separately from the fixed-stat ArmorEntity drop table above.
    @ManyToMany
    @JoinTable(
            name = "npc_items",
            joinColumns = @JoinColumn(name = "npc_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Collection<ItemEntity> itemDrops;
}
