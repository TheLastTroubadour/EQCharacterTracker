package com.eq.charactertracker.base;

import lombok.Data;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public abstract class BaseQuest {
    protected Boolean completed;
}
