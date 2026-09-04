package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.model.ServerDTO;
import com.eq.charactertracker.service.ServerService;
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
@RequestMapping("server")
public class ServerController {

    private final ServerService serverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ServerDTO createServer(@RequestBody ServerDTO server) {
        return serverService.create(server);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ServerDTO updateServer(@PathVariable Long id, @RequestBody ServerDTO server) {
        server.setId(id);
        return serverService.create(server);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ServerDTO> getServers(@RequestParam(name = "type", required = false) ServerType type) {
        return type != null ? serverService.findByType(type) : serverService.findAll();
    }
}
