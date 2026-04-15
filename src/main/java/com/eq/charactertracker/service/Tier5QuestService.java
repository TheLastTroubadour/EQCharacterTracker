package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.quest.thf.Tier5PoAAugmentQuestEntity;
import com.eq.charactertracker.entity.quest.thf.Tier5XegonyKeyQuestEntity;
import com.eq.charactertracker.model.quest.thf.Tier5PoAAugmentQuest;
import com.eq.charactertracker.model.quest.thf.Tier5XegonyKeyQuest;
import com.eq.charactertracker.repo.Tier5PoAAugmentQuestRepo;
import com.eq.charactertracker.repo.Tier5XegonyKeyQuestRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Tier5QuestService {

    private final Tier5XegonyKeyQuestRepo tier5XegonyKeyQuestRepo;
    private final Tier5PoAAugmentQuestRepo tier5PoAAugmentQuestRepo;
    private final ModelMapper modelMapper;

    public List<Tier5XegonyKeyQuest> getAllTier5XegonyQuests() {
        List<Tier5XegonyKeyQuestEntity> entities = tier5XegonyKeyQuestRepo.findAll();
        List<Tier5XegonyKeyQuest> list = new ArrayList<>();
        entities.forEach(e -> list.add(modelMapper.map(e, Tier5XegonyKeyQuest.class)));
        return list;
    }

    public List<Tier5PoAAugmentQuest> getAllTier5PoAAugmentQuests() {
        List<Tier5PoAAugmentQuest> list = new ArrayList<>();
        List<Tier5PoAAugmentQuestEntity> entityList = tier5PoAAugmentQuestRepo.findAll();
        entityList.forEach(e -> list.add(modelMapper.map(e, Tier5PoAAugmentQuest.class)));
        return list;
    }
}
