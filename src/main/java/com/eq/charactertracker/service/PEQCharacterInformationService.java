package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerInformation;
import com.eq.charactertracker.constants.peq.PEQWebAtttributesEnum;
import com.eq.charactertracker.model.Armor;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.WebItemInformation;
import com.eq.charactertracker.service.shared.BaseCharacterInformationService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        webItemInformation.setItemName(elements.select(".WindowTitleBar").text());
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
}
