CREATE TABLE expansions (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    short_name    VARCHAR(50)  NOT NULL,
    release_order INTEGER      NOT NULL DEFAULT 0,
    server        VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_expansion (short_name, server)
) ENGINE=InnoDB;

ALTER TABLE quest_templates
    ADD COLUMN progression_quest BIT          NOT NULL DEFAULT 0,
    ADD COLUMN expansion_id      BIGINT,
    ADD CONSTRAINT fk_quest_template_expansion FOREIGN KEY (expansion_id) REFERENCES expansions (id);
