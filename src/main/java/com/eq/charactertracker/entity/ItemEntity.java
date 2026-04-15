package com.eq.charactertracker.entity;

import com.eq.charactertracker.constants.ItemType;
import com.eq.charactertracker.constants.ServerEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private Long externalId;
    private String name;
    @Enumerated(EnumType.STRING)
    private ServerEnum server;
    private Short tier;

    @Enumerated(EnumType.STRING)
    private ItemType type;

}
