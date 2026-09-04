ALTER TABLE spells
    ADD COLUMN unlock_type       VARCHAR(16) NOT NULL DEFAULT 'NONE',
    ADD COLUMN rune_name         VARCHAR(255) NULL,
    ADD COLUMN rune_turn_in_order INTEGER NULL;
