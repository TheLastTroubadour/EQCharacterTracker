package com.eq.charactertracker.controller;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.model.CharacterEquipSlot;
import com.eq.charactertracker.service.CharacterEquipSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("character-equipped-items")
@CrossOrigin(origins = "*")
public class CharacterEquipSlotController {

    private final CharacterEquipSlotService characterEquipSlotService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("search")
    public List<CharacterEquipSlot> searchCharactersBySlotAndClass(@RequestParam(value = "slots", required = false) List<EquipSlotEnum> slots,
                                                                   @RequestParam(value = "classes", required = false) List<CharacterClass> classes,
                                                                   @RequestParam(value = "server", required = false) ServerEnum serverEnum) {
        return characterEquipSlotService.searchCharactersByClassesAndSlotsAndServer(classes, slots, serverEnum);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CharacterEquipSlot> getAllCharactersArmorSlots(){
        return characterEquipSlotService.getAllCharacterArmorSlots();
    }



}
