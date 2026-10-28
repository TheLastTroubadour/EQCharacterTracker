CREATE TABLE npc_items (
    npc_id  BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    PRIMARY KEY (npc_id, item_id),
    CONSTRAINT fk_npc_items_npc FOREIGN KEY (npc_id) REFERENCES npcs (id),
    CONSTRAINT fk_npc_items_item FOREIGN KEY (item_id) REFERENCES items (id)
) ENGINE=InnoDB;
