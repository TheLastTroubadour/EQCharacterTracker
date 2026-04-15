package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.QuestTemplateEntity;
import com.eq.charactertracker.model.quest.QuestTemplateRequest;
import com.eq.charactertracker.service.QuestTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("quest-template")
public class QuestTemplateController {

    private final QuestTemplateService questTemplateService;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestTemplateEntity> importTemplates(@RequestBody List<QuestTemplateRequest> requests) {
        return questTemplateService.importTemplates(requests);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<QuestTemplateEntity> getTemplates(
            @RequestParam(required = false) ServerType serverType,
            @RequestParam(required = false) CharacterClass characterClass) {
        return questTemplateService.getTemplates(serverType, characterClass);
    }
}
