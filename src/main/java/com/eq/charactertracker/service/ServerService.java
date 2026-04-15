package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.repo.ServerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepo serverRepo;

    public ServerEntity findByShortName(String shortName) {
        return serverRepo.findByShortName(shortName.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Unknown server: " + shortName));
    }

    public List<ServerEntity> findAll() {
        return serverRepo.findAll();
    }

    public List<ServerEntity> findByType(ServerType type) {
        return serverRepo.findByType(type);
    }

    public ServerEntity save(ServerEntity server) {
        return serverRepo.save(server);
    }
}
