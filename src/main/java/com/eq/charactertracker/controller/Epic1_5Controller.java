package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.quest.thf.Epic1_5Quest;
import com.eq.charactertracker.service.Epic1_5Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("epic-1-5")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class Epic1_5Controller {

    private final Epic1_5Service epic15Service;

    @GetMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Epic1_5Quest> getEpic1_5ByUserId(@PathVariable Long userId) {
        return epic15Service.getAllByUserId(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Epic1_5Quest> getAllEpic1_5() {
        return epic15Service.getAll();
    }

    @GetMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public List<Epic1_5Quest> updateAllEpic1_5Quests() {
        return epic15Service.updateAllEpic1_5Quest();
    }
}
