ALTER TABLE character_armor_by_slot
    ADD COLUMN aug3_id BIGINT,
    ADD CONSTRAINT fk_char_armor_aug3 FOREIGN KEY (aug3_id) REFERENCES augmentations (id);
