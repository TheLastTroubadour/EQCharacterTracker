package com.eq.charactertracker.entity.quest.thf;

import com.eq.charactertracker.entity.CharacterEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tier_5_PoA_Augment_Quest")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tier5PoAAugmentQuestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private QuestItemEntity questItem;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity windParchment;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity containerOfMist;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CharacterEntity character;

}
