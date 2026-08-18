ALTER TABLE quest_templates
    DROP COLUMN progression_quest,
    ADD COLUMN quest_type VARCHAR(50) NOT NULL;
