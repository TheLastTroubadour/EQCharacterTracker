package com.eq.charactertracker.entity;


import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.constants.ServerEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "augmentations")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AugmentationEntity extends BaseAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short augType;

    private Long extId;

    @Enumerated(EnumType.STRING)
    private ServerEnum server;
}
