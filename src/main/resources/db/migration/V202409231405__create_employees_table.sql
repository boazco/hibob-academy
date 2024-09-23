create table IF NOT EXISTS employees
(
    id UUID primary key default gen_random_uuid(),
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    role varchar(255) NOT NULL,
    department varchar(255) NOT NULL,
    company_id UUID NOT NULL
);