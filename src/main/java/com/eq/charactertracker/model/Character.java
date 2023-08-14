package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Character implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    private ServerEnum server;

    @NotNull
    private CharacterClass characterClass;

    private Short level;
    private Short aaSpent;
    private Short aaAvailable;

    private User user;
}
