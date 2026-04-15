package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.ItemType;
import com.eq.charactertracker.constants.ServerEnum;
import lombok.Data;

@Data
public class Item {

    private Long Id;
    private Long externalId;
    private String name;
    private ServerEnum server;
    private Short tier;
    private ItemType type;

}
