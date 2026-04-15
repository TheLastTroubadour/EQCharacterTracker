package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.model.User;
import com.eq.charactertracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserEntity getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User getUserById(Long userId) {
        UserEntity userEntity = userRepo.getReferenceById(userId);
        return modelMapper.map(userEntity, User.class);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll().stream()
                .map(ue -> modelMapper.map(ue, User.class))
                .collect(Collectors.toList());
    }

    public UserEntity createUser(String username, String email, String rawPassword) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode(rawPassword));
        return userRepo.save(userEntity);
    }
}
