package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.quest.CharacterQuestProgressResponse;
import com.eq.charactertracker.model.quest.RequirementProgressResponse;
import com.eq.charactertracker.model.quest.TeamQuestProgressResponse;
import com.eq.charactertracker.service.QuestProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("quest-progress")
public class QuestProgressController {

    private final QuestProgressService questProgressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CharacterQuestProgressResponse startQuest(
            @RequestParam("characterId") Long characterId,
            @RequestParam("questTemplateId") Long questTemplateId) {
        return questProgressService.startQuest(characterId, questTemplateId);
    }

    @GetMapping("/character/{characterId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CharacterQuestProgressResponse> getQuestProgress(@PathVariable Long characterId) {
        return questProgressService.getQuestProgressForCharacter(characterId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TeamQuestProgressResponse> getQuestProgressForUser(@PathVariable Long userId) {
        return questProgressService.getQuestProgressForUser(userId);
    }

    @GetMapping("/character/{characterId}/epic-status")
    @ResponseStatus(HttpStatus.OK)
    public boolean getEpicQuestStatus(@PathVariable Long characterId) {
        return questProgressService.isEpicQuestCompleted(characterId);
    }

    @PatchMapping("/{progressId}/requirement/{requirementProgressId}")
    @ResponseStatus(HttpStatus.OK)
    public RequirementProgressResponse toggleRequirement(
            @PathVariable Long progressId,
            @PathVariable Long requirementProgressId) {
        return questProgressService.toggleRequirement(progressId, requirementProgressId);
    }

    @PatchMapping("/{progressId}/complete")
    @ResponseStatus(HttpStatus.OK)
    public CharacterQuestProgressResponse setQuestCompletion(
            @PathVariable Long progressId,
            @RequestParam(name = "completed", defaultValue = "true") boolean completed) {
        return questProgressService.setQuestCompletion(progressId, completed);
    }
}
