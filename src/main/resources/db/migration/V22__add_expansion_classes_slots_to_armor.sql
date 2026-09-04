ALTER TABLE armor
    ADD COLUMN expansion_id BIGINT NULL,
    ADD CONSTRAINT fk_armor_expansion FOREIGN KEY (expansion_id) REFERENCES expansions (id);

CREATE TABLE armor_classes (
    armor_id        BIGINT      NOT NULL,
    character_class VARCHAR(32) NOT NULL,
    CONSTRAINT fk_armor_classes_armor FOREIGN KEY (armor_id) REFERENCES armor (id)
);

CREATE TABLE armor_slots (
    armor_id   BIGINT      NOT NULL,
    equip_slot VARCHAR(32) NOT NULL,
    CONSTRAINT fk_armor_slots_armor FOREIGN KEY (armor_id) REFERENCES armor (id)
);
