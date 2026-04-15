package com.eq.charactertracker.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotNull
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
}
