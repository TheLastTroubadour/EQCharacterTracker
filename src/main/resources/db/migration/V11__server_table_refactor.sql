-- V11: Replace server VARCHAR columns with FK to a servers table.
-- Also updates expansion and quest_template tables to use server_type (LIVE/PRIVATE).

CREATE TABLE servers (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    short_name VARCHAR(50)  NOT NULL UNIQUE,
    type       VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO servers (name, short_name, type) VALUES ('The Hidden Forest', 'THF', 'PRIVATE');
INSERT INTO servers (name, short_name, type) VALUES ('Teek', 'TEEK', 'LIVE');

-- ---- characters ----
ALTER TABLE characters ADD COLUMN server_id BIGINT;
UPDATE characters SET server_id = (SELECT id FROM servers WHERE short_name = server);
ALTER TABLE characters DROP INDEX uq_characters_name_server;
ALTER TABLE characters MODIFY server_id BIGINT NOT NULL;
ALTER TABLE characters ADD CONSTRAINT fk_characters_server FOREIGN KEY (server_id) REFERENCES servers (id);
ALTER TABLE characters ADD UNIQUE KEY uq_characters_name_server (name, server_id);
ALTER TABLE characters DROP COLUMN server;

-- ---- armor ----
ALTER TABLE armor ADD COLUMN server_id BIGINT;
UPDATE armor SET server_id = (SELECT id FROM servers WHERE short_name = server);
ALTER TABLE armor DROP INDEX uq_armor_ext_id_server;
ALTER TABLE armor MODIFY server_id BIGINT NOT NULL;
ALTER TABLE armor ADD CONSTRAINT fk_armor_server FOREIGN KEY (server_id) REFERENCES servers (id);
ALTER TABLE armor ADD UNIQUE KEY uq_armor_ext_id_server (ext_id, server_id);
ALTER TABLE armor DROP COLUMN server;

-- ---- augmentations ----
ALTER TABLE augmentations ADD COLUMN server_id BIGINT;
UPDATE augmentations SET server_id = (SELECT id FROM servers WHERE short_name = server);
ALTER TABLE augmentations DROP INDEX uq_aug_ext_id_server;
ALTER TABLE augmentations MODIFY server_id BIGINT NOT NULL;
ALTER TABLE augmentations ADD CONSTRAINT fk_augmentations_server FOREIGN KEY (server_id) REFERENCES servers (id);
ALTER TABLE augmentations ADD UNIQUE KEY uq_aug_ext_id_server (ext_id, server_id);
ALTER TABLE augmentations DROP COLUMN server;

-- ---- items ----
ALTER TABLE items ADD COLUMN server_id BIGINT;
UPDATE items SET server_id = (SELECT id FROM servers WHERE short_name = server);
ALTER TABLE items DROP INDEX uq_item_external_id_server;
ALTER TABLE items MODIFY server_id BIGINT NOT NULL;
ALTER TABLE items ADD CONSTRAINT fk_items_server FOREIGN KEY (server_id) REFERENCES servers (id);
ALTER TABLE items ADD UNIQUE KEY uq_item_external_id_server (external_id, server_id);
ALTER TABLE items DROP COLUMN server;

-- ---- expansions: replace server VARCHAR with server_type ----
ALTER TABLE expansions DROP INDEX uq_expansion;
ALTER TABLE expansions ADD COLUMN server_type VARCHAR(50) NOT NULL DEFAULT 'LIVE' AFTER release_order;
ALTER TABLE expansions DROP COLUMN server;
ALTER TABLE expansions ADD UNIQUE KEY uq_expansion (short_name, server_type);

-- ---- quest_templates: replace server VARCHAR with server_type ----
ALTER TABLE quest_templates DROP INDEX uq_quest_template;
ALTER TABLE quest_templates ADD COLUMN server_type VARCHAR(50) NOT NULL DEFAULT 'LIVE' AFTER ordered;
ALTER TABLE quest_templates DROP COLUMN server;
ALTER TABLE quest_templates ADD UNIQUE KEY uq_quest_template (name, server_type, character_class);
