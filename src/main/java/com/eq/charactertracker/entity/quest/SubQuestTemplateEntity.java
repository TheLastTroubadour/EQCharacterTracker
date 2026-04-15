package com.eq.charactertracker.entity.quest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_quest_templates")
@Getter
@Setter
@NoArgsConstructor
public class SubQuestTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int orderIndex;

    private boolean canSkip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_template_id", nullable = false)
    private QuestTemplateEntity questTemplate;

    @OneToMany(mappedBy = "subQuestTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<RequirementTemplateEntity> requirements = new ArrayList<>();
}
