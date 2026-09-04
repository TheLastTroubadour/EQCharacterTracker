package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.ArmorDTO;
import com.eq.charactertracker.model.imports.ArmorItemsFileImportResult;
import com.eq.charactertracker.service.ArmorItemsFileImportService;
import com.eq.charactertracker.service.ArmorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("armor")
public class ArmorController {

    private final ArmorItemsFileImportService armorItemsFileImportService;
    private final ArmorService armorService;

    @PostMapping("/import-from-items-file")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ArmorItemsFileImportResult importFromItemsFile(@RequestParam("filePath") String filePath) {
        return armorItemsFileImportService.importArmorFromFile(filePath);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArmorDTO> search(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) Long expansionId) {
        return armorService.search(name, expansionId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArmorDTO getById(@PathVariable Long id) {
        return armorService.getById(id);
    }
}
