package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.quest.thf.Tier5PoAAugmentQuest;
import com.eq.charactertracker.model.quest.thf.Tier5XegonyKeyQuest;
import com.eq.charactertracker.service.Tier5QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tier-5")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class Tier5QuestController {

    private final Tier5QuestService tier5QuestService;

    @GetMapping("xegony-keys")
    @ResponseStatus(HttpStatus.OK)
    public List<Tier5XegonyKeyQuest> getAllTier5XegonyKeyQuests(){
        return tier5QuestService.getAllTier5XegonyQuests();
    }

    @GetMapping("refresh")
    @ResponseStatus(HttpStatus.OK)
    public List<Tier5XegonyKeyQuest> refresh(){
        return tier5QuestService.updateTier5Quests();
    }

    @GetMapping("poa-augments")
    @ResponseStatus(HttpStatus.OK)
    public List<Tier5PoAAugmentQuest> getAllTier5PoAAugmentQuests(){
        return tier5QuestService.getAllTier5PoAAugmentQuests();
    }

}
