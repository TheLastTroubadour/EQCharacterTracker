CREATE TABLE armor_entity_click_effect (
    armor_entity_id BIGINT NOT NULL,
    click_effect    VARCHAR(255),
    CONSTRAINT fk_armor_click_effect FOREIGN KEY (armor_entity_id) REFERENCES armor (id)
) ENGINE=InnoDB;

CREATE TABLE augmentation_entity_click_effect (
    augmentation_entity_id BIGINT NOT NULL,
    click_effect            VARCHAR(255),
    CONSTRAINT fk_augmentation_click_effect FOREIGN KEY (augmentation_entity_id) REFERENCES augmentations (id)
) ENGINE=InnoDB;
