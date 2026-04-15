CREATE TABLE quest_templates (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    server          VARCHAR(255) NOT NULL,
    character_class VARCHAR(255),
    ordered         BIT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_quest_template (name, server, character_class)
) ENGINE=InnoDB;

CREATE TABLE sub_quest_templates (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(255) NOT NULL,
    order_index       INTEGER      NOT NULL DEFAULT 0,
    can_skip          BIT          NOT NULL DEFAULT 0,
    quest_template_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sub_quest_template FOREIGN KEY (quest_template_id) REFERENCES quest_templates (id)
) ENGINE=InnoDB;

CREATE TABLE requirement_templates (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    type                  VARCHAR(255) NOT NULL,
    description           VARCHAR(255),
    external_item_id      BIGINT,
    sub_quest_template_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_requirement_template FOREIGN KEY (sub_quest_template_id) REFERENCES sub_quest_templates (id)
) ENGINE=InnoDB;

CREATE TABLE character_quest_progress (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    character_id      BIGINT NOT NULL,
    quest_template_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_char_quest_progress (character_id, quest_template_id),
    CONSTRAINT fk_cqp_character     FOREIGN KEY (character_id)      REFERENCES characters (id),
    CONSTRAINT fk_cqp_quest_template FOREIGN KEY (quest_template_id) REFERENCES quest_templates (id)
) ENGINE=InnoDB;

CREATE TABLE requirement_progress (
    id                          BIGINT  NOT NULL AUTO_INCREMENT,
    character_quest_progress_id BIGINT  NOT NULL,
    requirement_template_id     BIGINT  NOT NULL,
    completed                   BIT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_requirement_progress (character_quest_progress_id, requirement_template_id),
    CONSTRAINT fk_rp_character_quest_progress FOREIGN KEY (character_quest_progress_id) REFERENCES character_quest_progress (id),
    CONSTRAINT fk_rp_requirement_template     FOREIGN KEY (requirement_template_id)     REFERENCES requirement_templates (id)
) ENGINE=InnoDB;
