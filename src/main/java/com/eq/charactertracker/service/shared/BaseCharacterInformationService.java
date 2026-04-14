package com.eq.charactertracker.service.shared;

import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.constants.peq.PEQWebAtttributesEnum;
import com.eq.charactertracker.constants.thf.THFWebAttributesEnum;
import com.eq.charactertracker.factory.CharacterInformationFactory;
import com.eq.charactertracker.model.Armor;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.CharacterEquipSlot;
import com.eq.charactertracker.model.WebItemInformation;
import com.eq.charactertracker.service.ArmorService;
import com.eq.charactertracker.service.CharacterEquipSlotService;
import com.eq.charactertracker.service.CharacterService;
import com.eq.charactertracker.service.DocumentService;
import com.eq.charactertracker.service.UserService;
import com.eq.charactertracker.util.CharacterArmorUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
public abstract class BaseCharacterInformationService implements ICharacterInformationService {

    @Autowired
    private CharacterService characterService;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;
    @Autowired
    private ArmorService armorService;

    @Autowired
    private CharacterEquipSlotService equipSlotService;

    @Autowired
    private CharacterInformationFactory characterInformationFactory;

    @Override
    public void updateExternalArmorDetailsForAllCharacters(List<Character> characterList) {
        characterList.forEach(c -> {
            ICharacterInformationService baseCharacterInformationService = characterInformationFactory.getInstance(c.getServer());
            baseCharacterInformationService.updateExternalArmorDetails(c);
        });
    }

    @Override
    public void updateExternalArmorDetails(Character character) {
        String characterUrl = getMageloUrl(character);
        CharacterEquipSlotService equipSlotService = getEquipSlotService();

        Document doc = documentService.openDocument(characterUrl);
        for(EquipSlotEnum slotEnum: EquipSlotEnum.values()) {
            Elements elements = doc.selectXpath(getWebItemInformationXPath(slotEnum));
            if(elements == null) {
                //maybe throw an exception?
                //log and get next one
                continue;
            }
            WebItemInformation webItemInformation = getArmorNameAndItemUrlAndItemExtId(elements);
            if(webItemInformation == null){
                //log and get next one
                continue;
            }
            Optional<Armor> armorOptional = getArmorInformation(character, webItemInformation);

            Armor armor = null;
            if (armorOptional.isPresent()) {
                armor = armorOptional.get();
                //need to update since aug broke it
                armor = saveArmor(armor, doc, slotEnum);

            } else {
                //If the item url is different than character url
                if (getItemUrl() != null) {
                    doc = documentService.openDocument(getItemUrl());
                }
                armor = saveArmor(character, doc, slotEnum, webItemInformation);
            }
            if (armor == null) {
                log.info(String.format("Unable to find information on armor for character %s with slot %s", character, slotEnum));
                continue;
            }
            Optional<CharacterEquipSlot> optionalCharacterEquipSlot = equipSlotService.findCharacterEquipSlotEntityByCharacterAndEquipSlot(character, slotEnum);
            CharacterEquipSlot characterEquipSlot = optionalCharacterEquipSlot.orElseGet(CharacterEquipSlot::new);

            characterEquipSlot.setEquipSlot(slotEnum);
            characterEquipSlot.setCharacter(character);
            characterEquipSlot.setArmor(armor);
            equipSlotService.save(characterEquipSlot);
        }
    }

    protected abstract String getMageloUrl(Character character);
    protected abstract Armor addItemStatInformationFromUrlToArmor(Armor armor, EquipSlotEnum slotEnum, Document document);
    protected abstract String getWebItemInformationXPath(EquipSlotEnum slotEnum);
    protected abstract WebItemInformation getArmorNameAndItemUrlAndItemExtId(Elements elements);

    protected String getItemUrl(){
        return null;
    }

    private Armor saveArmor(Armor armor, Document doc, EquipSlotEnum slotEnum){
        addItemStatInformationFromUrlToArmor(armor, slotEnum, doc);
        CharacterArmorUtil.setNullFieldsToZero(armor);
        return armorService.save(armor);
    }

    private Armor saveArmor(Character character, Document doc, EquipSlotEnum slotEnum, WebItemInformation webItemInformation){
        String itemName = webItemInformation.getItemName();
        Long itemExtId = webItemInformation.getItemExtId();
        Armor armor = Armor.builder()
                .name(itemName)
                .server(character.getServer())
                .extId(itemExtId)
                .build();
        return saveArmor(armor, doc, slotEnum);
    }
    private Optional<Armor> getArmorInformation(Character character, WebItemInformation webItemInformation){
        String itemName = webItemInformation.getItemName();
        log.info(String.format("Getting information on Armor: %s", itemName));
        String itemUrl = webItemInformation.getItemUrl();
        Long itemExtId = webItemInformation.getItemExtId();
        ServerEnum server = character.getServer();
        return armorService.findArmorEntityByExtIdAndServer(itemExtId, server);
    }

    protected Field replaceFieldValueOnArmor(Armor armor, PEQWebAtttributesEnum peqWebAtttributesEnum, String text){
                Field field = null;
        try {
            field = armor.getClass().getSuperclass().getDeclaredField(peqWebAtttributesEnum.getFieldName());

            String objectValue = !(text.contains("%")) ? text : text.substring(0, text.length() - 1);
            if (objectValue == null || objectValue.isBlank()) {
                objectValue = "0";
            }

            //Object obj = field.getType().equals(Integer.class) ? Integer.valueOf(objectValue) : Double.valueOf(objectValue);
            Object obj = Integer.valueOf(objectValue);
            if(field.getType().equals(Double.class)){
                obj = Double.valueOf(objectValue);
            }
            field.setAccessible(true);
            field.set(armor, obj);
        } catch (NoSuchFieldException e) {
            log.error("Unable to access field: " + peqWebAtttributesEnum.getFieldName() + " Field does not exist exception " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error("Unable to access field: " + field + " with illegal access: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
        return field;
    }

    protected Field replaceFieldValueOnArmor(Armor armor, THFWebAttributesEnum thfWebAttributesEnum, String text) {
        Field field = null;
        try {
            field = armor.getClass().getSuperclass().getDeclaredField(thfWebAttributesEnum.getFieldName());

            String objectValue = !(text.contains("%")) ? text : text.substring(0, text.length() - 1);
            if (objectValue == null || objectValue.isBlank()) {
                objectValue = "0";
            }

            //Object obj = field.getType().equals(Integer.class) ? Integer.valueOf(objectValue) : Double.valueOf(objectValue);
            Object obj = Integer.valueOf(objectValue);
            if(field.getType().equals(Double.class)){
                obj = Double.valueOf(objectValue);
            }
            field.setAccessible(true);
            field.set(armor, obj);
        } catch (NoSuchFieldException e) {
            log.error("Unable to access field: " + thfWebAttributesEnum.getFieldName() + " Field does not exist exception " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error("Unable to access field: " + field + " with illegal access: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
        return field;
    }
}
