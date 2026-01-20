--liquibase formatted sql

--changeset shoham-kar:init

CREATE TABLE cf_user (
                         id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1000) PRIMARY KEY,

                         username VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         user_role VARCHAR(50) NOT NULL,

                         created_on TIMESTAMP NOT NULL,
                         modified_on TIMESTAMP NOT NULL,
                         created_by BIGINT NOT NULL,
                         modified_by BIGINT NOT NULL
);

CREATE INDEX idx_cf_user_username ON cf_user (username);
CREATE INDEX idx_cf_user_email ON cf_user (email);
