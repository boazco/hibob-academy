create table IF NOT EXISTS feedback
(
    id UUID primary key default gen_random_uuid(),
    employee_id UUID,
    creation_date DATE default CURRENT_DATE,
    company_id UUID NOT NULL,
    status varchar(255) NOT NULL,
    feedback_message varchar(1000) NOT NULL
);