package com.eq.charactertracker.entity;

import com.eq.charactertracker.base.BaseAttributes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private ServerEntity server;
}
