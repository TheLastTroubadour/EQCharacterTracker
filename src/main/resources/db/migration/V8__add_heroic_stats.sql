ALTER TABLE armor
    ADD COLUMN heroic_strength     INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_stamina      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_agility      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_dexterity    INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_wisdom       INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_intelligence INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_charisma     INTEGER NOT NULL DEFAULT 0;

ALTER TABLE augmentations
    ADD COLUMN heroic_strength     INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_stamina      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_agility      INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_dexterity    INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_wisdom       INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_intelligence INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN heroic_charisma     INTEGER NOT NULL DEFAULT 0;
