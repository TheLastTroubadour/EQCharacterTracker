package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.model.UserDTO;
import com.eq.charactertracker.model.auth.AuthResponse;
import com.eq.charactertracker.model.auth.LoginRequest;
import com.eq.charactertracker.model.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        UserDTO user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserEntity user = (UserEntity) userService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
