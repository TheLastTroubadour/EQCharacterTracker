package com.eq.charactertracker.entity.quest;

import com.eq.charactertracker.entity.CharacterEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "character_quest_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"character_id", "quest_template_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CharacterQuestProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_template_id", nullable = false)
    private QuestTemplateEntity questTemplate;

    @OneToMany(mappedBy = "characterQuestProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequirementProgressEntity> requirementProgress = new ArrayList<>();
}
