package com.eq.charactertracker.entity.quest;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.ServerType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expansions", uniqueConstraints = @UniqueConstraint(columnNames = {"short_name", "server_type"}))
@Getter
@Setter
@NoArgsConstructor
public class ExpansionEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String shortName;

    private int releaseOrder;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;
}
