package com.eq.charactertracker.constants;

import com.eq.charactertracker.service.THFCharacterInformationService;
import com.eq.charactertracker.service.shared.BaseCharacterInformationService;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CharacterInformationEnum {

    THF("THF", THFCharacterInformationService.class);

    private final String serverShortName;
    private final Class<? extends BaseCharacterInformationService> serviceClass;

    private static final Map<String, CharacterInformationEnum> map = initMap();

    private static Map<String, CharacterInformationEnum> initMap() {
        return Arrays.stream(CharacterInformationEnum.values())
                .collect(Collectors.toMap(CharacterInformationEnum::getServerShortName, Function.identity()));
    }

    CharacterInformationEnum(String serverShortName, Class<? extends BaseCharacterInformationService> serviceClass) {
        this.serverShortName = serverShortName;
        this.serviceClass = serviceClass;
    }

    public static CharacterInformationEnum findByServerShortName(String shortName) {
        return map.get(shortName.toUpperCase());
    }
}
