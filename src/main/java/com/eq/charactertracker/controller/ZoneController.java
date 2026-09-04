package com.eq.charactertracker.controller;

import com.eq.charactertracker.model.ZoneCreateRequest;
import com.eq.charactertracker.model.ZoneDTO;
import com.eq.charactertracker.service.ZoneService;
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
@RequestMapping("zone")
public class ZoneController {

    private final ZoneService zoneService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ZoneDTO createZone(@RequestBody ZoneCreateRequest request) {
        return zoneService.createZone(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ZoneDTO> search(@RequestParam(required = false) String name) {
        return zoneService.search(name);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ZoneDTO getById(@PathVariable Long id) {
        return zoneService.getById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ZoneDTO updateZone(@PathVariable Long id, @RequestBody ZoneCreateRequest request) {
        return zoneService.updateZone(id, request);
    }
}
