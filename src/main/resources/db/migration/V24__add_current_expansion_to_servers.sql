ALTER TABLE servers
    ADD COLUMN current_expansion_id BIGINT NULL,
    ADD CONSTRAINT fk_server_current_expansion FOREIGN KEY (current_expansion_id) REFERENCES expansions (id);
