package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.CharacterClass;
import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.constants.ServerInformation;
import com.eq.charactertracker.constants.peq.PEQWebAtttributesEnum;
import com.eq.charactertracker.model.Armor;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.WebItemInformation;
import com.eq.charactertracker.service.shared.BaseCharacterInformationService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class PEQCharacterInformationService extends BaseCharacterInformationService {

    @Override
    protected String getWebItemInformationXPath(EquipSlotEnum slotEnum) {
        return "//*[@id=\"" + slotEnum.getSlotNumber() + "\"]";
    }

    @Override
    protected WebItemInformation getArmorNameAndItemUrlAndItemExtId(Elements elements) {
        WebItemInformation webItemInformation = new WebItemInformation();
        String itemName = elements.select(".WindowTitleBar").text();
        if(itemName == null || itemName.isBlank()){
            return null;
        }
        webItemInformation.setItemName(itemName);
        String itemUrl = elements.select("[href]").attr("href");
        webItemInformation.setItemUrl(itemUrl);
        String strItemExtId = itemUrl.substring(itemUrl.indexOf("?id=") + 4);
        Long itemExtId = Long.valueOf(strItemExtId);
        webItemInformation.setItemExtId(itemExtId);
        return webItemInformation;
    }

    @Override
    protected String getMageloUrl(Character character) {
        return ServerInformation.PEQ_MAGELO_URL + "?page=character&char=" + character.getName();
    }

    @Override
    protected Armor addItemStatInformationFromUrlToArmor(Armor armor, EquipSlotEnum slotEnum, Document document) {
        Elements itemStatElements = document.selectXpath("//*[@id='" + slotEnum.getSlotNumber() + "']");
        String augmentationNodeName = ".WindowNestedTan";
        Elements augStats = itemStatElements.select(augmentationNodeName);
        if(!augStats.isEmpty()){
            itemStatElements.select(augmentationNodeName).remove();
        }
        String itemStatsText = itemStatElements.select(".Stats").text();
        Arrays.stream(PEQWebAtttributesEnum.values()).forEach(v -> {
            String regex = String.format(".*%s \\+?([0-9]+).*", v.getHtmlName());
            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(itemStatsText);
            if(matcher.matches()){
               String value = matcher.group(1);
               replaceFieldValueOnArmor(armor, v, value);
            }
        });
        return armor;
    }

    @Override
    public List<Character> createAllCharactersByGuildName(Long userId, String guildName) {
        List<Character> characterList = new ArrayList<>();
        String url = ServerInformation.PEQ_MAGELO_URL + "?page=search&guild=" + guildName;

        Document document = getDocumentService().openDocument(url);
        Elements rows = document.selectXpath("//*[@id='charbrowser']//tbody/tr");
        characterList = rows
                .stream()
                .map(this::addCharacter)
                .collect(Collectors.toList());

        return getCharacterService().createCharacters(characterList, userId);
    }

    private Character addCharacter(Element element){
        Character character = new Character();
        String name = element.child(0).text();
        String strLevel = element.child(2).text();
        String strClass = element.child(3).text();
        //TODO better way to do this
        if(strClass.equals("Shadowknight")){
            strClass = "Shadow_Knight";
        }

        character.setName(name);
        character.setCharacterClass(CharacterClass.valueOf(strClass.toUpperCase()));
        character.setLevel(Short.valueOf(strLevel));
        character.setServer(ServerEnum.PEQ);

        return character;
    }
}
