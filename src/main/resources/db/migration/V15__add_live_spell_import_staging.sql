-- Staging table for the raw "Live" spell data CSV export (spells_new-style dump:
-- every spell, discipline, AA effect, and item clicky in the game, ~412 columns
-- in the source file). Only the columns useful for building the curated `spells`
-- catalog are kept. Re-importable: `id` is the source spell id and is the primary
-- key, so a fresh CSV dump can be reloaded with an upsert rather than a fresh table.

CREATE TABLE live_spell_import (
    id                BIGINT       NOT NULL,
    name              VARCHAR(255) NOT NULL,
    classes           VARCHAR(255),
    warlevel          SMALLINT,
    clrlevel          SMALLINT,
    pallevel          SMALLINT,
    rnglevel          SMALLINT,
    shdlevel          SMALLINT,
    drulevel          SMALLINT,
    brdlevel          SMALLINT,
    mnklevel          SMALLINT,
    roglevel          SMALLINT,
    shmlevel          SMALLINT,
    neclevel          SMALLINT,
    wizlevel          SMALLINT,
    maglevel          SMALLINT,
    enclevel          SMALLINT,
    bstlevel          SMALLINT,
    berlevel          SMALLINT,
    manacost          INTEGER,
    endurance_cost    INTEGER,
    castingtime       DOUBLE,
    skill             VARCHAR(100),
    targettype        VARCHAR(100),
    spelltype         VARCHAR(100),
    resist            VARCHAR(100),
    spell_group_rank  SMALLINT,
    expansion         VARCHAR(100),
    source            VARCHAR(50),
    deletable         SMALLINT,
    desc1             VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;
