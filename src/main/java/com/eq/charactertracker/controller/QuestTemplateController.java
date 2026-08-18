package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.model.quest.QuestTemplateRequest;
import com.eq.charactertracker.model.quest.QuestTemplateDTO;
import com.eq.charactertracker.service.QuestTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @PreAuthorize("hasRole('ADMIN')")
    public List<QuestTemplateDTO> importTemplates(@RequestBody List<QuestTemplateRequest> requests) {
        return questTemplateService.importTemplates(requests);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public QuestTemplateDTO updateTemplate(@PathVariable Long id, @RequestBody QuestTemplateRequest request) {
        return questTemplateService.updateTemplate(id, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<QuestTemplateDTO> getTemplates(
            @RequestParam(name = "serverType", required = false) ServerType serverType,
            @RequestParam(name = "characterClass", required = false) CharacterClass characterClass,
            @RequestParam(name = "name", required = false) String name) {
        return questTemplateService.getTemplates(serverType, characterClass, name);
    }
}
