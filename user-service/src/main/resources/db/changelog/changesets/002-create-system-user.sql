--liquibase formatted sql

--changeset shoham-kar:create-system-user
INSERT INTO cf_user (id, username, password, email, user_role, created_on, modified_on, created_by, modified_by)
OVERRIDING SYSTEM VALUE
VALUES (0, 'payroll-service', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy990i2', 'payroll-service@constructflow.local', 'ADMIN', NOW(), NOW(), 0, 0)
ON CONFLICT (username) DO NOTHING;
