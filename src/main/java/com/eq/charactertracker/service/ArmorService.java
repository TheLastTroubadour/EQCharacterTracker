package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.ArmorDTO;
import com.eq.charactertracker.repo.ArmorRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArmorService extends BaseServiceImpl<ArmorEntity, ArmorDTO> {

    private final ArmorRepo armorRepo;
    private final ServerService serverService;

    public ArmorService(ModelMapper modelMapper, ArmorRepo armorRepo, ServerService serverService) {
        super(modelMapper, armorRepo);
        this.armorRepo = armorRepo;
        this.serverService = serverService;
    }

    public Optional<ArmorDTO> findArmorEntityByExtIdAndServer(Long extItemId, String serverShortName) {
        ServerEntity server = serverService.findByShortName(serverShortName);
        return armorRepo.findArmorEntityByExtIdAndServer(extItemId, server).map(this::entityToDTO);
    }

    public ArmorDTO save(ArmorDTO armor) {
        ArmorEntity armorEntity = dtoToEntity(armor);
        if (armor.getServer() != null) {
            armorEntity.setServer(serverService.findByShortName(armor.getServer()));
        }
        armorEntity = armorRepo.save(armorEntity);
        return entityToDTO(armorEntity);
    }
}
