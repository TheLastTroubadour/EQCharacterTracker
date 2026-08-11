package com.eq.charactertracker.service;

import com.eq.charactertracker.constants.ItemType;
import com.eq.charactertracker.constants.ServerInformation;
import com.eq.charactertracker.entity.CharacterEntity;
import com.eq.charactertracker.entity.CharacterInventoryEntity;
import com.eq.charactertracker.entity.ItemEntity;
import com.eq.charactertracker.entity.ServerEntity;
import com.eq.charactertracker.model.CharacterDTO;
import com.eq.charactertracker.repo.CharacterInventoryRepo;
import com.eq.charactertracker.repo.CharacterRepo;
import com.eq.charactertracker.repo.ItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class THFItemService {

    private final CharacterRepo characterRepo;
    private final ModelMapper modelMapper;
    private final ItemRepo itemRepo;
    private final CharacterInventoryRepo characterInventoryRepo;
    private final ServerService serverService;

    private static final String THF_SHORT_NAME = "THF";

    public List<CharacterDTO> updateInventoryInformation(Long userId) {
        List<CharacterEntity> characterEntityList = characterRepo.findCharacterEntitiesByUserId(userId);
        List<CharacterDTO> characterList = new ArrayList<>();
        for (CharacterEntity character : characterEntityList) {
            updateInventoryInformation(character);
            characterList.add(modelMapper.map(character, CharacterDTO.class));
        }
        return characterList;
    }

    public List<CharacterDTO> updateInventoryInformation(List<CharacterDTO> characterList) {
        characterList.forEach(c -> updateInventoryInformation(c));
        return characterList;
    }

    public CharacterDTO updateInventoryInformation(CharacterDTO character) {
        ServerEntity server = serverService.findByShortName(character.getServer());
        CharacterEntity characterEntity = characterRepo.findCharacterEntityByNameAndServer(character.getName(), server);
        updateInventoryInformation(characterEntity);
        return character;
    }

    public CharacterEntity updateInventoryInformation(CharacterEntity character) {
        String characterUrl = ServerInformation.THF_MAGELO_URL + "character=" + character.getName();
        try {
            Document doc = Jsoup.connect(characterUrl).get();
            Elements itemInventoryElements = doc.selectXpath("//*[@id='viewer']/div[@class='item']");
            for (Element e : itemInventoryElements) {
                String slotName = e.attr("id");
                Element itemElement = e.children().select("a[href]").first();
                String itemUrl = ServerInformation.THF_WEBTOOLS_URL + itemElement.attr("href");
                Long itemExtId = Long.valueOf(itemUrl.substring(itemUrl.indexOf("?id=") + 4));
                String itemName = itemElement.text();
                log.debug(String.format("Item Trying to build %s for character %s", itemName, character.getName()));
                if (itemName.equals("") || itemName == null) {
                    continue;
                }
                Optional<ItemEntity> optionalItemEntity = itemRepo.findItemEntityByExternalIdAndServer(itemExtId, character.getServer());
                ItemEntity itemEntity = null;
                if (optionalItemEntity.isPresent()) {
                    itemEntity = optionalItemEntity.get();
                } else {
                    itemEntity = createItemEntityFromExternalId(itemExtId);
                }
                Optional<CharacterInventoryEntity> characterInventoryEntity = characterInventoryRepo.findCharacterInventoryEntityBySlotNameAndCharacter_Id(slotName, character.getId());
                CharacterInventoryEntity cie = null;
                if (characterInventoryEntity.isPresent()) {
                    cie = characterInventoryEntity.get();
                    if (cie.getItem().getExternalId().equals(itemEntity.getExternalId())) {
                        continue;
                    }
                }

                if (cie == null) {
                    cie = new CharacterInventoryEntity();
                }

                cie.setItem(itemEntity);
                cie.setCharacter(character);
                cie.setSlotName(slotName);

                characterInventoryRepo.save(cie);
            }
        } catch (IOException e) {
            log.error(String.format("Unable to update inventory information for characterId: %s Exception: %s", character.getId(), e.getMessage()));
        }
        return character;
    }

    public ItemEntity createItemEntityFromExternalId(Long externalId) {
        ServerEntity thfServer = serverService.findByShortName(THF_SHORT_NAME);
        Document itemUrlDoc = null;
        try {
            itemUrlDoc = Jsoup.connect(ServerInformation.THF_ITEM_LOOKUP_URL + externalId).get();
            Elements elements = itemUrlDoc.selectXpath("//*[@class='item']/table[@class='table']/tbody/tr");
            Element itemNameElement = itemUrlDoc.selectXpath("//*[@class='item']/p[@class='title-item']/a").first();
            String tierElementText = elements.get(0).text();
            String tierPattern = "Tier: ([0-9]+)";
            Pattern tp = Pattern.compile(tierPattern);
            Matcher m = tp.matcher(tierElementText);
            String tier = m.find() ? m.group(1) : "";

            String typeElementText = elements.get(1).text();
            String typePattern = "Type: (\\w+) ";
            Pattern typ = Pattern.compile(typePattern);
            Matcher mTy = typ.matcher(typeElementText);
            ItemType type = ItemType.fromWebName(mTy.find() ? mTy.group(1) : "");
            ItemEntity itemEntity = ItemEntity.builder()
                    .externalId(externalId)
                    .name(itemNameElement.text())
                    .server(thfServer)
                    .tier(Short.valueOf(tier))
                    .type(type)
                    .build();
            itemRepo.save(itemEntity);
            return itemEntity;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemEntity createOrGetItemEntity(Long externalId, String serverShortName) {
        ServerEntity server = serverService.findByShortName(serverShortName);
        Optional<ItemEntity> entity = itemRepo.findItemEntityByExternalIdAndServer(externalId, server);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            return createItemEntityFromExternalId(externalId);
        }
    }
}
