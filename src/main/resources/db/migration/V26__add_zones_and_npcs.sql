CREATE TABLE zones (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    expansion_id  BIGINT       NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_zone_name UNIQUE (name),
    CONSTRAINT fk_zone_expansion FOREIGN KEY (expansion_id) REFERENCES expansions (id)
) ENGINE=InnoDB;

CREATE TABLE npcs (
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    name  VARCHAR(255) NOT NULL,
    tier  VARCHAR(16)  NOT NULL DEFAULT 'GROUP_NAMED',
    PRIMARY KEY (id),
    CONSTRAINT uq_npc_name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE npc_zones (
    npc_id  BIGINT NOT NULL,
    zone_id BIGINT NOT NULL,
    PRIMARY KEY (npc_id, zone_id),
    CONSTRAINT fk_npc_zones_npc FOREIGN KEY (npc_id) REFERENCES npcs (id),
    CONSTRAINT fk_npc_zones_zone FOREIGN KEY (zone_id) REFERENCES zones (id)
) ENGINE=InnoDB;
