--liquibase formatted sql

--changeset shoham-kar:add-active-field

ALTER TABLE address ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE customer ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE company ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE cost_code ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE project ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;
ALTER TABLE task ADD COLUMN active BOOLEAN DEFAULT TRUE NOT NULL;

-- employee and project_budget already have active field from 001-init.sql
-- but they might not have NOT NULL constraint. Let's make sure they are consistent.
-- Since they were created with DEFAULT TRUE, existing rows should have it, but for safety:
UPDATE employee SET active = TRUE WHERE active IS NULL;
ALTER TABLE employee ALTER COLUMN active SET DEFAULT TRUE;
ALTER TABLE employee ALTER COLUMN active SET NOT NULL;

UPDATE project_budget SET active = TRUE WHERE active IS NULL;
ALTER TABLE project_budget ALTER COLUMN active SET DEFAULT TRUE;
ALTER TABLE project_budget ALTER COLUMN active SET NOT NULL;
