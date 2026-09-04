package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.repo.ServerRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// TODO: migrate to BaseServiceImpl pattern (needs a ServerDTO first, see feedback_service_architecture memory)
@Service
public class ServerService extends BaseServiceImpl<ServerEntity, ServerDTO> {

    private final ServerRepo serverRepo;

    public ServerService(ModelMapper modelMapper, ServerRepo serverRepo) {
        super(modelMapper, serverRepo);
        this.serverRepo = serverRepo;
    }

    @Deprecated(since = "Use findByName")
    public Optional<ServerEntity> findByShortName(String shortName) {
        return serverRepo.findByShortName(shortName.toUpperCase());
    }

    public ServerDTO findByName(String serverName){
        Optional<ServerEntity> server = serverRepo.findByShortName(serverName);
        if(server.isPresent()){
            return entityToDTO(server.get());
        }
        server = serverRepo.findByNameIgnoreCase(serverName);
        if(server.isPresent()){
            return entityToDTO(server.get());
        }
        throw new IllegalArgumentException("Unknown Server " + serverName);
    }

    public List<ServerDTO> findByType(ServerType type) {
        return serverRepo.findByType(type).stream().map(this::entityToDTO).toList();
    }

}
