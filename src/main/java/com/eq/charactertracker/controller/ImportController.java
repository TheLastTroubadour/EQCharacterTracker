package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.imports.CharacterImportRequest;
import com.eq.charactertracker.model.imports.ImportResult;
import com.eq.charactertracker.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("import")
public class ImportController {

    private final ImportService importService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ImportResult> importCharacters(@RequestBody List<CharacterImportRequest> requests) {
        return importService.importCharacters(requests);
    }
}
