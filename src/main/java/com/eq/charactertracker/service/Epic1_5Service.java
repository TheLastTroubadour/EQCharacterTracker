package com.eq.charactertracker.service;

import com.eq.charactertracker.entity.quest.thf.Epic1_5QuestEntity;
import com.eq.charactertracker.model.quest.thf.Epic1_5Quest;
import com.eq.charactertracker.repo.Epic1_5Repo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Epic1_5Service {

    private final Epic1_5Repo repo;
    private final ModelMapper modelMapper;

    public List<Epic1_5Quest> getAllByUserId(Long userId) {
        List<Epic1_5QuestEntity> entities = repo.findAllByCharacter_User_Id(userId);
        List<Epic1_5Quest> models = new ArrayList<>();
        entities.forEach(e -> models.add(modelMapper.map(e, Epic1_5Quest.class)));
        return models;
    }

    public List<Epic1_5Quest> getAll() {
        List<Epic1_5QuestEntity> entities = repo.findAllByOrderByCompleted();
        List<Epic1_5Quest> models = new ArrayList<>();
        entities.forEach(e -> models.add(modelMapper.map(e, Epic1_5Quest.class)));
        return models;
    }
}
