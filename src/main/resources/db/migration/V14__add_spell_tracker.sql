CREATE TABLE spells (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    spell_rank      SMALLINT     NOT NULL DEFAULT 1,
    character_class VARCHAR(255) NOT NULL,
    server_type     VARCHAR(255) NOT NULL,
    level           SMALLINT     NOT NULL DEFAULT 0,
    mana_cost       INTEGER,
    description     VARCHAR(255),
    expansion_id    BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_spell (name, spell_rank, character_class, server_type),
    CONSTRAINT fk_spell_expansion FOREIGN KEY (expansion_id) REFERENCES expansions (id)
) ENGINE=InnoDB;

CREATE TABLE character_spells (
    id           BIGINT  NOT NULL AUTO_INCREMENT,
    character_id BIGINT  NOT NULL,
    spell_id     BIGINT  NOT NULL,
    scribed      BIT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_character_spell (character_id, spell_id),
    CONSTRAINT fk_char_spell_character FOREIGN KEY (character_id) REFERENCES characters (id),
    CONSTRAINT fk_char_spell_spell     FOREIGN KEY (spell_id)     REFERENCES spells (id)
) ENGINE=InnoDB;
