package com.eq.charactertracker.model;

import com.eq.charactertracker.constants.ItemType;
import lombok.Data;

@Data
public class Item {

    private Long Id;
    private Long externalId;
    private String name;
    private String server;
    private Short tier;
    private ItemType type;

}
