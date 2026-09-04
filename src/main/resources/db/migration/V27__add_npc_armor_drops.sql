CREATE TABLE npc_armor (
    npc_id   BIGINT NOT NULL,
    armor_id BIGINT NOT NULL,
    PRIMARY KEY (npc_id, armor_id),
    CONSTRAINT fk_npc_armor_npc FOREIGN KEY (npc_id) REFERENCES npcs (id),
    CONSTRAINT fk_npc_armor_armor FOREIGN KEY (armor_id) REFERENCES armor (id)
) ENGINE=InnoDB;
