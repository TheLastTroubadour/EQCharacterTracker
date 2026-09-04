package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zones", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "expansion_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ZoneEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Not unique on its own -- a zone can be revamped and reused under the same name in a
    // later expansion (e.g. "The Temple of Veeshan" in Scars of Velious vs. Claws of
    // Veeshan). Uniqueness only holds per (name, expansion), same idea as NpcEntity.name
    // being scoped per zone rather than global.
    @Column(nullable = false)
    private String name;

    private String shortName;

    // The expansion the zone belongs to, derived from which expansion's Hunter/Hunts
    // achievement category the zone's entry was found under.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expansion_id")
    private ExpansionEntity expansion;
}
