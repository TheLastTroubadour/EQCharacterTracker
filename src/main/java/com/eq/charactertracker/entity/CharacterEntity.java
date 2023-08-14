package com.eq.charactertracker.entity;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
    private ServerEnum server;
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private CharacterClass characterClass;
    private Short level;
    private Short aaSpent;
    private Short aaAvailable;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
