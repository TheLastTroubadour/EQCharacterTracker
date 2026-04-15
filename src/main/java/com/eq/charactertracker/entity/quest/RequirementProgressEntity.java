package com.eq.charactertracker.entity.quest;

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
@Table(name = "requirement_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"character_quest_progress_id", "requirement_template_id"}))
@Getter
@Setter
@NoArgsConstructor
public class RequirementProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_quest_progress_id", nullable = false)
    private CharacterQuestProgressEntity characterQuestProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_template_id", nullable = false)
    private RequirementTemplateEntity requirementTemplate;

    private boolean completed;
}
