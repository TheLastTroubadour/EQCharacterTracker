package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.quest.ExpansionEntity;
import com.eq.charactertracker.repo.quest.ExpansionRepo;
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
@RequestMapping("expansion")
public class ExpansionController {

    private final ExpansionRepo expansionRepo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpansionEntity createExpansion(@RequestBody ExpansionEntity expansion) {
        return expansionRepo.save(expansion);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExpansionEntity> getExpansions(@RequestParam ServerType serverType) {
        return expansionRepo.findByServerTypeOrderByReleaseOrderAsc(serverType);
    }
}
