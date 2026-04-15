-- V7: Clean up duplicate TEEK data from failed import attempts and add unique
-- constraints on armor/augmentations/items to prevent future duplicates.

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM character_inventory
WHERE character_id IN (SELECT id FROM characters WHERE server = 'TEEK');

DELETE FROM character_armor_by_slot
WHERE character_id IN (SELECT id FROM characters WHERE server = 'TEEK');

DELETE FROM characters WHERE server = 'TEEK';
DELETE FROM items WHERE server = 'TEEK';
DELETE FROM augmentations WHERE server = 'TEEK';
DELETE FROM armor WHERE server = 'TEEK';

SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE armor ADD UNIQUE KEY uq_armor_ext_id_server (ext_id, server);
ALTER TABLE augmentations ADD UNIQUE KEY uq_aug_ext_id_server (ext_id, server);
ALTER TABLE items ADD UNIQUE KEY uq_item_external_id_server (external_id, server);
