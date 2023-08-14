package com.eq.charactertracker.factory;

import com.eq.charactertracker.constants.CharacterInformationEnum;
import com.eq.charactertracker.constants.ServerEnum;
import com.eq.charactertracker.service.shared.ICharacterInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CharacterInformationFactory {

    @Autowired
    private List<ICharacterInformationService> characterInformationServiceList;

    private Map<Class<? extends ICharacterInformationService>, ICharacterInformationService> baseCharacterInformationServiceMap;

    public ICharacterInformationService getInstance(ServerEnum server) {
        Class clazz = CharacterInformationEnum.findByServerName(server).getServiceClass();
        return baseCharacterInformationServiceMap.get(clazz);
    }

    @PostConstruct
    private Map<Class<? extends ICharacterInformationService>, ICharacterInformationService> initMap(){
        baseCharacterInformationServiceMap = new HashMap<>();
        baseCharacterInformationServiceMap = characterInformationServiceList.stream().collect(Collectors.toMap(e -> e.getClass(), Function.identity()));
        return baseCharacterInformationServiceMap;
    }
}
