package com.eq.charactertracker.constants;

import lombok.Getter;

@Getter
public enum ServerEnum {
    THF("The Hidden Forest");

    private final String serverName;
    ServerEnum(String serverName) {
        this.serverName = serverName;
    }

}
