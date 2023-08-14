package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.EquipSlotEnum;
import com.eq.charactertracker.constants.ServerInformation;
import com.eq.charactertracker.constants.thf.THFWebAttributesEnum;
import com.eq.charactertracker.model.Armor;
import com.eq.charactertracker.model.Character;
import com.eq.charactertracker.model.WebItemInformation;
import com.eq.charactertracker.service.shared.BaseCharacterInformationService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class THFCharacterInformationService extends BaseCharacterInformationService {

    @Override
    protected String getMageloUrl(Character character) {
        return ServerInformation.THF_MAGELO_URL + "character=" + character.getName();
    }

    @Override
    protected WebItemInformation getArmorNameAndItemUrlAndItemExtId(Elements elements) {
        WebItemInformation webItemInformation = new WebItemInformation();
        Element element = elements.get(0);
        webItemInformation.setItemName(element.text());
        String itemUrl = element.attr("href");
        webItemInformation.setItemUrl(itemUrl);
        webItemInformation.setItemExtId(Long.valueOf(itemUrl.substring(itemUrl.indexOf("?id=") + 4)));
        return webItemInformation;
    }

    @Override
    protected Armor addItemStatInformationFromUrlToArmor(Armor armor, EquipSlotEnum equipSlotEnum, Document document) {
        Document itemDoc = null;
        String itemUrl = getItemUrl();
        if(itemUrl != null) {
            itemDoc = getDocumentService().openDocument(itemUrl);
        }
        Elements trElements = itemDoc.body().selectXpath("//*[@class='table']//*[@class='display-item']/tbody/tr");
            for(int i = 0; i < trElements.size(); i++){
                Element tr = trElements.get(i);
                Elements tdElements = tr.children();
                //they are always 3 elements going to make an assumption to cut down on time
                String traitName = tdElements.get(0).text().trim();
                THFWebAttributesEnum thfWebAttribute =THFWebAttributesEnum.getTHFWebAttributeByHtmlName(traitName);
                if(thfWebAttribute != null){
                    //BaseAttributes class
                    String text = tdElements.get(1).text();
                    replaceFieldValueOnArmor(armor, thfWebAttribute, text);
                }
            }
        return armor;
    }

    @Override
    protected String getWebItemInformationXPath(EquipSlotEnum slotEnum) {
        return "//*[@id='" + slotEnum.getSlotNumber() + "']/*[@class='title-item']/a";
    }
}
