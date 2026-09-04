package com.eq.charactertracker.entity.spell;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.constants.spell.SpellUnlockType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "spells", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "spell_rank", "character_class", "server_type"}))
@Getter
@Setter
@NoArgsConstructor
public class SpellEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "spell_rank")
    private short rank = 1;

    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    private short level;

    private Integer manaCost;

    private Integer enduranceCost;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expansion_id")
    private ExpansionEntity expansion;

    @Enumerated(EnumType.STRING)
    @Column(name = "unlock_type", nullable = false)
    private SpellUnlockType unlockType = SpellUnlockType.NONE;

    // Multiple levels can share the same rune (e.g. 81/82 both need rune A). Runes must be
    // turned in in ascending order -- you can't get rune C until you've turned in A and B --
    // so a lower runeTurnInOrder is always a hard prerequisite for a higher one.
    @Column(name = "rune_name")
    private String runeName;

    @Column(name = "rune_turn_in_order")
    private Integer runeTurnInOrder;
}
