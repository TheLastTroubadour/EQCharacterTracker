package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.ArmorDTO;
import com.eq.charactertracker.repo.ArmorRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<ArmorDTO> search(String name, Long expansionId) {
        String namePattern = (name == null || name.isBlank()) ? null : "%" + name.toLowerCase() + "%";
        List<ArmorEntity> armor = armorRepo.search(namePattern, expansionId);
        return armor.stream().map(this::entityToDTO).toList();
    }

    public Optional<ArmorDTO> findArmorEntityByExtIdAndServer(Long extItemId, String serverShortName) {
        ServerEntity server = getModelMapper().map(serverService.findByName(serverShortName), ServerEntity.class);
        return armorRepo.findArmorEntityByExtIdAndServer(extItemId, server).map(this::entityToDTO);
    }

    public ArmorDTO save(ArmorDTO armor) {
        ArmorEntity armorEntity = dtoToEntity(armor);
        if (armor.getServer() != null) {
            armorEntity.setServer(getModelMapper().map(serverService.findByName(armor.getServer()), ServerEntity.class));
        }
        armorEntity = armorRepo.save(armorEntity);
        return entityToDTO(armorEntity);
    }
}
