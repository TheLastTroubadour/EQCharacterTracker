package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.Role;
import com.eq.charactertracker.entity.UserEntity;
import com.eq.charactertracker.model.UserDTO;
import com.eq.charactertracker.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends BaseServiceImpl<UserEntity, UserDTO> implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(ModelMapper modelMapper, UserRepo userRepo, PasswordEncoder passwordEncoder) {
        super(modelMapper, userRepo);
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

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

    public UserDTO getUserById(Long userId) {
        return getById(userId);
    }

    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream().map(this::entityToDTO).toList();
    }

    public UserEntity createUser(String username, String email, String rawPassword) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode(rawPassword));
        userEntity.setRole(Role.USER);
        return userRepo.save(userEntity);
    }
}
