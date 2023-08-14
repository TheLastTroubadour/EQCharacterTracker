package com.eq.charactertracker.constants;

import com.eq.charactertracker.service.PEQCharacterInformationService;
import com.eq.charactertracker.service.THFCharacterInformationService;
import com.eq.charactertracker.service.shared.BaseCharacterInformationService;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CharacterInformationEnum {

    THF(ServerEnum.THF, THFCharacterInformationService.class),
    PEQ(ServerEnum.PEQ, PEQCharacterInformationService.class);

    private ServerEnum server;
    private Class<? extends BaseCharacterInformationService> serviceClass;

    private final static Map<ServerEnum, CharacterInformationEnum> map = initMap();

    private static Map<ServerEnum, CharacterInformationEnum> initMap() {
        return Arrays.stream(CharacterInformationEnum.values()).collect(Collectors.toMap(e -> e.getServer(), Function.identity()));
    }

    CharacterInformationEnum(ServerEnum serverEnum, Class<? extends BaseCharacterInformationService> serviceClass) {
        this.server = serverEnum;
        this.serviceClass = serviceClass;
    }

    public static CharacterInformationEnum findByServerName(ServerEnum server){
        return map.get(server);
    }


}
