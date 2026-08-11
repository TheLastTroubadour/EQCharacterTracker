package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.model.quest.ExpansionDTO;
import com.eq.charactertracker.service.ExpansionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("expansion")
public class ExpansionController {

    private final ExpansionService expansionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ExpansionDTO createExpansion(@RequestBody ExpansionEntity expansion) {
        return expansionService.createExpansion(expansion);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExpansionDTO> getExpansions(@RequestParam("serverType") ServerType serverType) {
        return expansionService.getExpansions(serverType);
    }
}
