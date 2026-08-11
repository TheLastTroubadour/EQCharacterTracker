package com.eq.charactertracker.entity.spell;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.entity.CharacterEntity;
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
@Table(name = "character_spells", uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "spell_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CharacterSpellEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spell_id", nullable = false)
    private SpellEntity spell;

    private boolean scribed;
}
