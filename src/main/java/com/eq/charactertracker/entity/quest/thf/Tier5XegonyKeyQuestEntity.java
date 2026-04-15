package com.eq.charactertracker.entity.quest.thf;

import com.eq.charactertracker.base.BaseTier5Quest;
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
@Table(name = "tier_5_xegony_key_quest")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tier5XegonyKeyQuestEntity extends BaseTier5Quest {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity mysticalEssenceOfDust;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity mysticalEssenceOfMist;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity mysticalEssenceOfSmoke;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity mysticalEssenceOfWind;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CharacterEntity character;
}
