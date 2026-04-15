package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.ArmorEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.Armor;
import com.eq.charactertracker.repo.ArmorRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArmorService {

    private final ArmorRepo armorRepo;
    private final ModelMapper modelMapper;
    private final ServerService serverService;

    public Optional<Armor> findArmorEntityByExtIdAndServer(Long extItemId, String serverShortName) {
        ServerEntity server = serverService.findByShortName(serverShortName);
        Optional<ArmorEntity> armorEntity = armorRepo.findArmorEntityByExtIdAndServer(extItemId, server);
        if (armorEntity.isPresent()) {
            Armor armor = modelMapper.map(armorEntity.get(), Armor.class);
            return Optional.of(armor);
        }
        return Optional.empty();
    }

    public Armor save(Armor armor) {
        ArmorEntity armorEntity = modelMapper.map(armor, ArmorEntity.class);
        if (armor.getServer() != null) {
            armorEntity.setServer(serverService.findByShortName(armor.getServer()));
        }
        armorEntity = armorRepo.save(armorEntity);
        return modelMapper.map(armorEntity, Armor.class);
    }
}
