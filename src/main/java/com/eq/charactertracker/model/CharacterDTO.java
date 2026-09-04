package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.CharacterClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private ServerDTO server;

    @NotNull
    private CharacterClass characterClass;

    private Short level;
    private Short aaSpent;
    private Short aaAvailable;

    private UserDTO user;
}
