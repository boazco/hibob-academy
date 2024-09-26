create table IF NOT EXISTS response
(
    id UUID primary key default gen_random_uuid(),
    employee_id UUID NOT NULL,
    creation_date DATE default CURRENT_DATE,
    company_id UUID NOT NULL,
    feedback_id UUID NOT NULL,
    response_message TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_feedback_id on response(feedback_id);
