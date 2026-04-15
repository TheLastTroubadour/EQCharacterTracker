package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.auth.AuthResponse;
import com.eq.charactertracker.model.auth.LoginRequest;
import com.eq.charactertracker.model.auth.RegisterRequest;
import com.eq.charactertracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
