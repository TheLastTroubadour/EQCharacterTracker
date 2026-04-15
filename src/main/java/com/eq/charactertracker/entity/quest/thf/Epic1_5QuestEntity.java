package com.eq.charactertracker.entity.quest.thf;

import com.eq.charactertracker.base.BaseQuest;
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
@Table(name = "epic_1_5_items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Epic1_5QuestEntity extends BaseQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity firstStone;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity secondStone;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity thirdStone;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity fourthStone;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity flawlessUnicornHeart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity perfectUnicornHeart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity totalUnicornHeart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity absurdUnicornHeart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity eyeOfTheTerrorantula;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity eyeOfTheSpectre;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity eyeOfTheVampire;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity eyeOfTheGhost;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QuestItemEntity bloodOfTheDragon;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CharacterEntity character;


}
