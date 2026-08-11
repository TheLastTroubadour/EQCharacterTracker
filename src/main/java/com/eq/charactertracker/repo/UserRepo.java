package com.eq.charactertracker.repo;

import com.eq.charactertracker.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends BaseRepo<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
