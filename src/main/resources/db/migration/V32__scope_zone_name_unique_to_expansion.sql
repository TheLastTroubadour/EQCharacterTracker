-- Zone names aren't globally unique -- a zone can be revamped and reused under the same
-- name in a later expansion (e.g. "The Temple of Veeshan" exists in both Scars of Velious
-- and Claws of Veeshan as genuinely different zones). Uniqueness only holds per (name, expansion).
ALTER TABLE zones DROP INDEX uq_zone_name;
ALTER TABLE zones ADD CONSTRAINT uq_zone_name_expansion UNIQUE (name, expansion_id);
