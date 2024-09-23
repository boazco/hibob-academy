create table IF NOT EXISTS company
(
    id UUID primary key default gen_random_uuid(),
    name varchar(255) NOT NULL
);