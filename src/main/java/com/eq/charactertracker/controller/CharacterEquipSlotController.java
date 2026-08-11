package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.model.CharacterEquipSlotDTO;
import com.eq.charactertracker.service.CharacterEquipSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("character-equipped-items")
public class CharacterEquipSlotController {

    private final CharacterEquipSlotService characterEquipSlotService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("search")
    public List<CharacterEquipSlotDTO> searchCharactersBySlotAndClass(
            @RequestParam(value = "slots", required = false) List<EquipSlotEnum> slots,
            @RequestParam(value = "classes", required = false) List<CharacterClass> classes,
            @RequestParam(value = "server", required = false) String server) {
        return characterEquipSlotService.searchCharactersByClassesAndSlotsAndServer(classes, slots, server);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CharacterEquipSlotDTO> getAllCharactersArmorSlots() {
        return characterEquipSlotService.getAllCharacterArmorSlots();
    }
}
