package com.eq.charactertracker.service;

import com.eq.charactertracker.base.BaseServiceImpl;
import com.eq.charactertracker.entity.ZoneEntity;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.model.ZoneCreateRequest;
import com.eq.charactertracker.model.ZoneDTO;
import com.eq.charactertracker.repo.ZoneRepo;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ZoneService extends BaseServiceImpl<ZoneEntity, ZoneDTO> {

    private final ZoneRepo zoneRepo;
    private final ExpansionRepo expansionRepo;

    public ZoneService(ModelMapper modelMapper, ZoneRepo zoneRepo, ExpansionRepo expansionRepo) {
        super(modelMapper, zoneRepo);
        this.zoneRepo = zoneRepo;
        this.expansionRepo = expansionRepo;
    }

    public List<ZoneDTO> search(String name) {
        List<ZoneEntity> zones = (name == null || name.isBlank())
                ? zoneRepo.findAll()
                : zoneRepo.findByNameContainingIgnoreCase(name);
        return zones.stream().map(this::entityToDTO).toList();
    }

    @Transactional
    public ZoneDTO createZone(ZoneCreateRequest request) {
        ZoneEntity zone = applyRequest(new ZoneEntity(), request);
        zone = zoneRepo.save(zone);
        return entityToDTO(zone);
    }

    @Transactional
    public ZoneDTO updateZone(Long id, ZoneCreateRequest request) {
        ZoneEntity zone = zoneRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown zone id: " + id));
        zone = applyRequest(zone, request);
        zone = zoneRepo.save(zone);
        return entityToDTO(zone);
    }

    private ZoneEntity applyRequest(ZoneEntity zone, ZoneCreateRequest request) {
        zone.setName(request.getName());
        zone.setShortName(request.getShortName());

        if (request.getExpansionId() != null) {
            ExpansionEntity expansion = expansionRepo.findById(request.getExpansionId())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown expansion id: " + request.getExpansionId()));
            zone.setExpansion(expansion);
        }
        return zone;
    }
}
