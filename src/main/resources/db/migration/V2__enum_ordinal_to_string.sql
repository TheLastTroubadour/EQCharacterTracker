-- V2: Convert enum columns from ORDINAL (INT) to STRING (VARCHAR)
-- Hibernate 6 expects VARCHAR for @Enumerated(EnumType.STRING).
-- Original ordinals (ServerEnum had PEQ=0, THF=1 before PEQ was removed):
--   ServerEnum:    0=PEQ (deleted), 1=THF
--   CharacterClass: 0=WARRIOR, 1=CLERIC, 2=PALADIN, 3=RANGER, 4=SHADOW_KNIGHT,
--                   5=DRUID, 6=BARD, 7=ROGUE, 8=SHAMAN, 9=NECROMANCER,
--                   10=WIZARD, 11=MAGICIAN, 12=ENCHANTER, 13=BEASTLORD,
--                   14=BERSERKER, 15=MONK
--   EquipSlotEnum:  0=CHARM, 1=LEFT_EAR, 2=HEAD, 3=FACE, 4=RIGHT_EAR,
--                   5=NECK, 6=SHOULDER, 7=ARMS, 8=BACK, 9=LEFT_WRIST,
--                   10=RIGHT_WRIST, 11=RANGE, 12=HANDS, 13=PRIMARY,
--                   14=SECONDARY, 15=LEFT_FINGER, 16=RIGHT_FINGER,
--                   17=CHEST, 18=LEGS, 19=FEET, 20=WAIST

-- Disable FK checks so parent rows can be deleted before child cleanup cascades
SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------
-- armor.server
-- -------------------------
ALTER TABLE armor ADD COLUMN server_str VARCHAR(255);
UPDATE armor SET server_str = CASE server WHEN 1 THEN 'THF' ELSE NULL END;
DELETE FROM armor WHERE server_str IS NULL;
ALTER TABLE armor DROP COLUMN server;
ALTER TABLE armor CHANGE server_str server VARCHAR(255);

-- -------------------------
-- augmentations.server
-- -------------------------
ALTER TABLE augmentations ADD COLUMN server_str VARCHAR(255);
UPDATE augmentations SET server_str = CASE server WHEN 1 THEN 'THF' ELSE NULL END;
DELETE FROM augmentations WHERE server_str IS NULL;
ALTER TABLE augmentations DROP COLUMN server;
ALTER TABLE augmentations CHANGE server_str server VARCHAR(255);

-- -------------------------
-- items.server
-- -------------------------
ALTER TABLE items ADD COLUMN server_str VARCHAR(255);
UPDATE items SET server_str = CASE server WHEN 1 THEN 'THF' ELSE NULL END;
DELETE FROM items WHERE server_str IS NULL;
ALTER TABLE items DROP COLUMN server;
ALTER TABLE items CHANGE server_str server VARCHAR(255);

-- -------------------------
-- characters.server
-- -------------------------
ALTER TABLE characters ADD COLUMN server_str VARCHAR(255);
UPDATE characters SET server_str = CASE server WHEN 1 THEN 'THF' ELSE NULL END;
DELETE FROM characters WHERE server_str IS NULL;
ALTER TABLE characters DROP COLUMN server;
ALTER TABLE characters CHANGE server_str server VARCHAR(255);

-- -------------------------
-- characters.character_class
-- -------------------------
ALTER TABLE characters ADD COLUMN character_class_str VARCHAR(255);
UPDATE characters SET character_class_str = CASE character_class
    WHEN 0  THEN 'WARRIOR'
    WHEN 1  THEN 'CLERIC'
    WHEN 2  THEN 'PALADIN'
    WHEN 3  THEN 'RANGER'
    WHEN 4  THEN 'SHADOW_KNIGHT'
    WHEN 5  THEN 'DRUID'
    WHEN 6  THEN 'BARD'
    WHEN 7  THEN 'ROGUE'
    WHEN 8  THEN 'SHAMAN'
    WHEN 9  THEN 'NECROMANCER'
    WHEN 10 THEN 'WIZARD'
    WHEN 11 THEN 'MAGICIAN'
    WHEN 12 THEN 'ENCHANTER'
    WHEN 13 THEN 'BEASTLORD'
    WHEN 14 THEN 'BERSERKER'
    WHEN 15 THEN 'MONK'
    ELSE NULL
END;
ALTER TABLE characters DROP COLUMN character_class;
ALTER TABLE characters CHANGE character_class_str character_class VARCHAR(255);

-- -------------------------
-- character_armor_by_slot.equip_slot
-- -------------------------
ALTER TABLE character_armor_by_slot ADD COLUMN equip_slot_str VARCHAR(255);
UPDATE character_armor_by_slot SET equip_slot_str = CASE equip_slot
    WHEN 0  THEN 'CHARM'
    WHEN 1  THEN 'LEFT_EAR'
    WHEN 2  THEN 'HEAD'
    WHEN 3  THEN 'FACE'
    WHEN 4  THEN 'RIGHT_EAR'
    WHEN 5  THEN 'NECK'
    WHEN 6  THEN 'SHOULDER'
    WHEN 7  THEN 'ARMS'
    WHEN 8  THEN 'BACK'
    WHEN 9  THEN 'LEFT_WRIST'
    WHEN 10 THEN 'RIGHT_WRIST'
    WHEN 11 THEN 'RANGE'
    WHEN 12 THEN 'HANDS'
    WHEN 13 THEN 'PRIMARY'
    WHEN 14 THEN 'SECONDARY'
    WHEN 15 THEN 'LEFT_FINGER'
    WHEN 16 THEN 'RIGHT_FINGER'
    WHEN 17 THEN 'CHEST'
    WHEN 18 THEN 'LEGS'
    WHEN 19 THEN 'FEET'
    WHEN 20 THEN 'WAIST'
    ELSE NULL
END;
ALTER TABLE character_armor_by_slot DROP COLUMN equip_slot;
ALTER TABLE character_armor_by_slot CHANGE equip_slot_str equip_slot VARCHAR(255);

SET FOREIGN_KEY_CHECKS = 1;
