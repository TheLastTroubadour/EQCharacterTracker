-- After V2 dropped the equip_slot INT column, the composite unique key
-- uq_char_armor_slot was left covering only character_id.
-- MySQL uses that index to back the fk_char_armor_character FK, so we must
-- add a temporary index on character_id before dropping the broken unique key.
ALTER TABLE character_armor_by_slot ADD INDEX idx_char_armor_char_tmp (character_id);
ALTER TABLE character_armor_by_slot DROP INDEX uq_char_armor_slot;
ALTER TABLE character_armor_by_slot ADD UNIQUE KEY uq_char_armor_slot (character_id, equip_slot);
-- The new composite key (character_id, equip_slot) now backs the FK, so drop the temp index.
ALTER TABLE character_armor_by_slot DROP INDEX idx_char_armor_char_tmp;
