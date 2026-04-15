package com.eq.charactertracker.entity;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "characters", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "server"}))
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ServerEnum server;
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;
    private Short level;
    private Short aaSpent;
    private Short aaAvailable;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
