package com.eq.charactertracker.entity;


import com.eq.charactertracker.base.BaseAttributes;
import com.eq.charactertracker.constants.ServerEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

    private ServerEnum server;
}
