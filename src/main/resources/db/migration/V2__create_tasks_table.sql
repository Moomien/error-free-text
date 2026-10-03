CREATE TABLE error_free_text.tasks (
    id              UUID        PRIMARY KEY,
    text            TEXT        NOT NULL,
    language        VARCHAR(2)  NOT NULL,
    status          VARCHAR(20) NOT NULL,
    corrected_text  TEXT,
    error_message   TEXT,
    created_at      TIMESTAMP   NOT NULL,
    updated_at      TIMESTAMP   NOT NULL
);

CREATE INDEX idx_tasks_status_created
    ON error_free_text.tasks(status, created_at);