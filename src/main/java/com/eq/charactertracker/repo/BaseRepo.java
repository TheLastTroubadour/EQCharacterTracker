package com.eq.charactertracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepo <T, K> extends JpaRepository<T, K> {
}
