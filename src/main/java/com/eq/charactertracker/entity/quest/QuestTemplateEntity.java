package com.eq.charactertracker.entity.quest;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.QuestType;
import com.eq.charactertracker.constants.ServerType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quest_templates", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "server_type", "character_class"}))
@Getter
@Setter
@NoArgsConstructor
public class QuestTemplateEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    private boolean ordered;

    @Enumerated(EnumType.STRING)
    private QuestType questType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expansion_id")
    private ExpansionEntity expansion;

    @OneToMany(mappedBy = "questTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<SubQuestTemplateEntity> subQuests = new ArrayList<>();
}
