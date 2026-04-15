-- V1: Initial schema
-- Represents the schema as created by Hibernate ddl-auto=update.
-- This migration is baselining an existing database, so it will not run
-- against an existing DB. It serves as the source of truth for fresh installs.

CREATE TABLE users (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    email    VARCHAR(255),
    password VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE characters (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255),
    server          VARCHAR(255),
    character_class VARCHAR(255),
    level           SMALLINT,
    aa_spent        SMALLINT,
    aa_available    SMALLINT,
    user_id         BIGINT   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_characters_name_server (name, server),
    CONSTRAINT fk_characters_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE armor (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    ext_id           BIGINT,
    server           VARCHAR(255),
    name             VARCHAR(255) NOT NULL,
    ac               INTEGER      NOT NULL DEFAULT 0,
    strength         INTEGER      NOT NULL DEFAULT 0,
    stamina          INTEGER      NOT NULL DEFAULT 0,
    agility          INTEGER      NOT NULL DEFAULT 0,
    dexterity        INTEGER      NOT NULL DEFAULT 0,
    wisdom           INTEGER      NOT NULL DEFAULT 0,
    intelligence     INTEGER      NOT NULL DEFAULT 0,
    charisma         INTEGER      NOT NULL DEFAULT 0,
    hit_points       INTEGER      NOT NULL DEFAULT 0,
    mana_points      INTEGER      NOT NULL DEFAULT 0,
    endurance_points INTEGER      NOT NULL DEFAULT 0,
    fire_resist      INTEGER      NOT NULL DEFAULT 0,
    cold_resist      INTEGER      NOT NULL DEFAULT 0,
    magic_resist     INTEGER      NOT NULL DEFAULT 0,
    disease_resist   INTEGER      NOT NULL DEFAULT 0,
    poison_resist    INTEGER      NOT NULL DEFAULT 0,
    attack           INTEGER      NOT NULL DEFAULT 0,
    accuracy         DOUBLE       NOT NULL DEFAULT 0,
    avoidance        DOUBLE       NOT NULL DEFAULT 0,
    combat_effect    DOUBLE       NOT NULL DEFAULT 0,
    stun_resist      DOUBLE       NOT NULL DEFAULT 0,
    damage_shield    INTEGER      NOT NULL DEFAULT 0,
    dot_shielding    DOUBLE       NOT NULL DEFAULT 0,
    endurance_regen  INTEGER      NOT NULL DEFAULT 0,
    hit_points_regen INTEGER      NOT NULL DEFAULT 0,
    mana_points_regen INTEGER     NOT NULL DEFAULT 0,
    spell_shielding  DOUBLE       NOT NULL DEFAULT 0,
    shielding        DOUBLE       NOT NULL DEFAULT 0,
    strikethrough    DOUBLE       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE armor_entity_focus_effect (
    armor_entity_id BIGINT      NOT NULL,
    focus_effect    VARCHAR(255),
    CONSTRAINT fk_armor_focus_effect FOREIGN KEY (armor_entity_id) REFERENCES armor (id)
) ENGINE=InnoDB;

CREATE TABLE augmentations (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    aug_type         SMALLINT,
    ext_id           BIGINT,
    server           VARCHAR(255),
    name             VARCHAR(255) NOT NULL,
    ac               INTEGER      NOT NULL DEFAULT 0,
    strength         INTEGER      NOT NULL DEFAULT 0,
    stamina          INTEGER      NOT NULL DEFAULT 0,
    agility          INTEGER      NOT NULL DEFAULT 0,
    dexterity        INTEGER      NOT NULL DEFAULT 0,
    wisdom           INTEGER      NOT NULL DEFAULT 0,
    intelligence     INTEGER      NOT NULL DEFAULT 0,
    charisma         INTEGER      NOT NULL DEFAULT 0,
    hit_points       INTEGER      NOT NULL DEFAULT 0,
    mana_points      INTEGER      NOT NULL DEFAULT 0,
    endurance_points INTEGER      NOT NULL DEFAULT 0,
    fire_resist      INTEGER      NOT NULL DEFAULT 0,
    cold_resist      INTEGER      NOT NULL DEFAULT 0,
    magic_resist     INTEGER      NOT NULL DEFAULT 0,
    disease_resist   INTEGER      NOT NULL DEFAULT 0,
    poison_resist    INTEGER      NOT NULL DEFAULT 0,
    attack           INTEGER      NOT NULL DEFAULT 0,
    accuracy         DOUBLE       NOT NULL DEFAULT 0,
    avoidance        DOUBLE       NOT NULL DEFAULT 0,
    combat_effect    DOUBLE       NOT NULL DEFAULT 0,
    stun_resist      DOUBLE       NOT NULL DEFAULT 0,
    damage_shield    INTEGER      NOT NULL DEFAULT 0,
    dot_shielding    DOUBLE       NOT NULL DEFAULT 0,
    endurance_regen  INTEGER      NOT NULL DEFAULT 0,
    hit_points_regen INTEGER      NOT NULL DEFAULT 0,
    mana_points_regen INTEGER     NOT NULL DEFAULT 0,
    spell_shielding  DOUBLE       NOT NULL DEFAULT 0,
    shielding        DOUBLE       NOT NULL DEFAULT 0,
    strikethrough    DOUBLE       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE augmentation_entity_focus_effect (
    augmentation_entity_id BIGINT      NOT NULL,
    focus_effect           VARCHAR(255),
    CONSTRAINT fk_augmentation_focus_effect FOREIGN KEY (augmentation_entity_id) REFERENCES augmentations (id)
) ENGINE=InnoDB;

CREATE TABLE items (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    external_id BIGINT,
    name        VARCHAR(255),
    server      VARCHAR(255),
    tier        SMALLINT,
    type        VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE character_armor_by_slot (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    equip_slot   VARCHAR(255),
    character_id BIGINT      NOT NULL,
    armor_id     BIGINT      NOT NULL,
    aug1_id      BIGINT,
    aug2_id      BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_char_armor_slot (character_id, equip_slot),
    CONSTRAINT fk_char_armor_character  FOREIGN KEY (character_id) REFERENCES characters (id),
    CONSTRAINT fk_char_armor_armor      FOREIGN KEY (armor_id)     REFERENCES armor (id),
    CONSTRAINT fk_char_armor_aug1       FOREIGN KEY (aug1_id)      REFERENCES augmentations (id),
    CONSTRAINT fk_char_armor_aug2       FOREIGN KEY (aug2_id)      REFERENCES augmentations (id)
) ENGINE=InnoDB;

CREATE TABLE character_inventory (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    slot_name    VARCHAR(255),
    item_id      BIGINT       NOT NULL,
    character_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_char_inventory_slot (slot_name, character_id),
    CONSTRAINT fk_char_inventory_item      FOREIGN KEY (item_id)      REFERENCES items (id),
    CONSTRAINT fk_char_inventory_character FOREIGN KEY (character_id) REFERENCES characters (id)
) ENGINE=InnoDB;

CREATE TABLE progression_flags (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    flag_name    VARCHAR(255),
    completed    BOOLEAN      NOT NULL DEFAULT FALSE,
    character_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_flags_character FOREIGN KEY (character_id) REFERENCES characters (id)
) ENGINE=InnoDB;

CREATE TABLE quest_item_chart (
    id           BIGINT  NOT NULL AUTO_INCREMENT,
    item_id      BIGINT,
    in_inventory BIT,
    PRIMARY KEY (id),
    CONSTRAINT fk_quest_item FOREIGN KEY (item_id) REFERENCES items (id)
) ENGINE=InnoDB;

CREATE TABLE epic_1_5_items (
    id                        BIGINT  NOT NULL AUTO_INCREMENT,
    completed                 BIT,
    first_stone_id            BIGINT,
    second_stone_id           BIGINT,
    third_stone_id            BIGINT,
    fourth_stone_id           BIGINT,
    flawless_unicorn_heart_id BIGINT,
    perfect_unicorn_heart_id  BIGINT,
    total_unicorn_heart_id    BIGINT,
    absurd_unicorn_heart_id   BIGINT,
    eye_of_the_terrorantula_id BIGINT,
    eye_of_the_spectre_id     BIGINT,
    eye_of_the_vampire_id     BIGINT,
    eye_of_the_ghost_id       BIGINT,
    blood_of_the_dragon_id    BIGINT,
    character_id              BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_epic15_first_stone             FOREIGN KEY (first_stone_id)             REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_second_stone            FOREIGN KEY (second_stone_id)            REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_third_stone             FOREIGN KEY (third_stone_id)             REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_fourth_stone            FOREIGN KEY (fourth_stone_id)            REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_flawless_unicorn        FOREIGN KEY (flawless_unicorn_heart_id)  REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_perfect_unicorn         FOREIGN KEY (perfect_unicorn_heart_id)   REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_total_unicorn           FOREIGN KEY (total_unicorn_heart_id)     REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_absurd_unicorn          FOREIGN KEY (absurd_unicorn_heart_id)    REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_eye_terrorantula        FOREIGN KEY (eye_of_the_terrorantula_id) REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_eye_spectre             FOREIGN KEY (eye_of_the_spectre_id)      REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_eye_vampire             FOREIGN KEY (eye_of_the_vampire_id)      REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_eye_ghost               FOREIGN KEY (eye_of_the_ghost_id)        REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_blood_dragon            FOREIGN KEY (blood_of_the_dragon_id)     REFERENCES quest_item_chart (id),
    CONSTRAINT fk_epic15_character               FOREIGN KEY (character_id)               REFERENCES characters (id)
) ENGINE=InnoDB;

CREATE TABLE Tier_5_PoA_Augment_Quest (
    id                   BIGINT NOT NULL AUTO_INCREMENT,
    quest_item_id        BIGINT,
    wind_parchment_id    BIGINT,
    container_of_mist_id BIGINT,
    character_id         BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_tier5poa_quest_item        FOREIGN KEY (quest_item_id)        REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5poa_wind_parchment    FOREIGN KEY (wind_parchment_id)    REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5poa_container_of_mist FOREIGN KEY (container_of_mist_id) REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5poa_character         FOREIGN KEY (character_id)         REFERENCES characters (id)
) ENGINE=InnoDB;

CREATE TABLE tier_5_xegony_key_quest (
    id                          BIGINT NOT NULL AUTO_INCREMENT,
    completed                   BIT,
    mystical_essence_of_dust_id  BIGINT,
    mystical_essence_of_mist_id  BIGINT,
    mystical_essence_of_smoke_id BIGINT,
    mystical_essence_of_wind_id  BIGINT,
    character_id                BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_tier5xeg_dust      FOREIGN KEY (mystical_essence_of_dust_id)  REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5xeg_mist      FOREIGN KEY (mystical_essence_of_mist_id)  REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5xeg_smoke     FOREIGN KEY (mystical_essence_of_smoke_id) REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5xeg_wind      FOREIGN KEY (mystical_essence_of_wind_id)  REFERENCES quest_item_chart (id),
    CONSTRAINT fk_tier5xeg_character FOREIGN KEY (character_id)                 REFERENCES characters (id)
) ENGINE=InnoDB;
