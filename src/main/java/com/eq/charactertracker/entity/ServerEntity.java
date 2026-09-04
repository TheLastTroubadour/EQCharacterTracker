package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseEntity;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import jakarta.persistence.Column;
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
@Table(name = "servers")
@Getter
@Setter
@NoArgsConstructor
public class ServerEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String shortName;

    @Enumerated(EnumType.STRING)
    private ServerType type;

    // The latest expansion this (typically PRIVATE) server has actually unlocked -- lets
    // "missing" comparisons (spells, gear, etc.) exclude content the server hasn't reached
    // yet, e.g. not flagging Ring of Scale spells as missing on a server still in EoK.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_expansion_id")
    private ExpansionEntity currentExpansion;
}
