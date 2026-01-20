--liquibase formatted sql

--changeset shoham-kar:init

CREATE TABLE address (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         created_on TIMESTAMP NOT NULL,
                         modified_on TIMESTAMP NOT NULL,
                         created_by BIGINT NOT NULL,
                         modified_by BIGINT NOT NULL,

                         address_line1 VARCHAR(255),
                         address_line2 VARCHAR(255),
                         city VARCHAR(255) NOT NULL,
                         state VARCHAR(255) NOT NULL,
                         postal_code VARCHAR(255),
                         country VARCHAR(255)
);

CREATE TABLE customer (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          created_on TIMESTAMP NOT NULL,
                          modified_on TIMESTAMP NOT NULL,
                          created_by BIGINT NOT NULL,
                          modified_by BIGINT NOT NULL,

                          name VARCHAR(255) NOT NULL,
                          code VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE company (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         created_on TIMESTAMP NOT NULL,
                         modified_on TIMESTAMP NOT NULL,
                         created_by BIGINT NOT NULL,
                         modified_by BIGINT NOT NULL,

                         name VARCHAR(255) NOT NULL,
                         code VARCHAR(255) NOT NULL UNIQUE,
                         address_id BIGINT,
                         customer_id BIGINT,

                         CONSTRAINT fk_company_address FOREIGN KEY (address_id) REFERENCES address (id),
                         CONSTRAINT fk_company_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE UNIQUE INDEX idx_company_address_id ON company (address_id) WHERE address_id IS NOT NULL;
CREATE INDEX idx_company_customer_id ON company (customer_id);


CREATE TABLE cost_code (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           created_on TIMESTAMP NOT NULL,
                           modified_on TIMESTAMP NOT NULL,
                           created_by BIGINT NOT NULL,
                           modified_by BIGINT NOT NULL,

                           name VARCHAR(255) NOT NULL,
                           code VARCHAR(255) NOT NULL UNIQUE,
                           cost_code_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
                           parent_id BIGINT,
                           customer_id BIGINT,

                           CONSTRAINT fk_cost_code_parent FOREIGN KEY (parent_id) REFERENCES cost_code (id),
                           CONSTRAINT fk_cost_code_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE INDEX idx_cost_code_parent_id ON cost_code (parent_id);
CREATE INDEX idx_cost_code_customer_id ON cost_code (customer_id);

CREATE TABLE employee (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          created_on TIMESTAMP NOT NULL,
                          modified_on TIMESTAMP NOT NULL,
                          created_by BIGINT NOT NULL,
                          modified_by BIGINT NOT NULL,

                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          pay_rate DOUBLE PRECISION,
                          employee_type VARCHAR(50) NOT NULL DEFAULT 'HOURLY',
                          employee_role VARCHAR(50) NOT NULL DEFAULT 'WORKER',
                          company_id BIGINT,
                          address_id BIGINT,
                          user_id BIGINT NOT NULL UNIQUE,
                          active BOOLEAN DEFAULT TRUE,

                          CONSTRAINT fk_employee_company FOREIGN KEY (company_id) REFERENCES company (id),
                          CONSTRAINT fk_employee_address FOREIGN KEY (address_id) REFERENCES address (id)
);

CREATE INDEX idx_employee_company_id ON employee (company_id);
CREATE UNIQUE INDEX idx_employee_address_id ON employee (address_id) WHERE address_id IS NOT NULL;
CREATE UNIQUE INDEX idx_employee_user_id ON employee (user_id);

CREATE TABLE project (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         created_on TIMESTAMP NOT NULL,
                         modified_on TIMESTAMP NOT NULL,
                         created_by BIGINT NOT NULL,
                         modified_by BIGINT NOT NULL,

                         name VARCHAR(255) NOT NULL,
                         code VARCHAR(255) NOT NULL UNIQUE,
                         project_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
                         customer_id BIGINT,
                         address_id BIGINT,

                         CONSTRAINT fk_project_customer FOREIGN KEY (customer_id) REFERENCES customer (id),
                         CONSTRAINT fk_project_address FOREIGN KEY (address_id) REFERENCES address (id)
);

CREATE INDEX idx_project_customer_id ON project (customer_id);
CREATE UNIQUE INDEX idx_project_address_id ON project (address_id) WHERE address_id IS NOT NULL;

CREATE TABLE task (
                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      created_on TIMESTAMP NOT NULL,
                      modified_on TIMESTAMP NOT NULL,
                      created_by BIGINT NOT NULL,
                      modified_by BIGINT NOT NULL,

                      name VARCHAR(255) NOT NULL,
                      code VARCHAR(255) NOT NULL UNIQUE,
                      task_status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
                      project_id BIGINT,

                      CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_project_id ON task (project_id);

CREATE TABLE project_budget (
                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                created_on TIMESTAMP NOT NULL,
                                modified_on TIMESTAMP NOT NULL,
                                created_by BIGINT NOT NULL,
                                modified_by BIGINT NOT NULL,

                                task_id BIGINT NOT NULL,
                                cost_code_id BIGINT NOT NULL,
                                active BOOLEAN NOT NULL DEFAULT TRUE,

                                CONSTRAINT fk_project_budget_task FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE,
                                CONSTRAINT fk_project_budget_cost_code FOREIGN KEY (cost_code_id) REFERENCES cost_code (id) ON DELETE CASCADE
);

CREATE INDEX idx_project_budget_task_id ON project_budget (task_id);
CREATE INDEX idx_project_budget_cost_code_id ON project_budget (cost_code_id);
