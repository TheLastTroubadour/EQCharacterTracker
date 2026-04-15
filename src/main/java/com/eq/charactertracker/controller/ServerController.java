package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.ServerType;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.service.ServerService;
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
@RequestMapping("server")
public class ServerController {

    private final ServerService serverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServerEntity createServer(@RequestBody ServerEntity server) {
        return serverService.save(server);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ServerEntity> getServers(@RequestParam(required = false) ServerType type) {
        return type != null ? serverService.findByType(type) : serverService.findAll();
    }
}
