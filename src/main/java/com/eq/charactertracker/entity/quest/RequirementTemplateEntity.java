package com.eq.charactertracker.entity.quest;

import com.eq.charactertracker.constants.RequirementType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "requirement_templates")
@Getter
@Setter
@NoArgsConstructor
public class RequirementTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequirementType type;

    private String description;

    private Long externalItemId;

    private String phrase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_quest_template_id", nullable = false)
    private SubQuestTemplateEntity subQuestTemplate;
}
