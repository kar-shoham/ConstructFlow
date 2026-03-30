--liquibase formatted sql

--changeset shoham-kar:add-timesheet-status
ALTER TABLE timesheet ADD COLUMN status VARCHAR(20) DEFAULT 'SUBMITTED' NOT NULL;
