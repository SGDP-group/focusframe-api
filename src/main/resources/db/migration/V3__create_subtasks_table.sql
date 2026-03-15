CREATE TABLE subtasks (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    description text NULL,
    task_id int NOT NULL,
    task_order int NOT NULL,
    start_time timestamp(6) NULL,
    end_time timestamp(6) NULL,
    duration int NULL,
    estimated_time int NULL,
    completed bool NOT NULL,
    productive int NULL,
    is_tracked bool NOT NULL,
    is_ai_breakdown bool NOT NULL,
    status_id int NOT NULL,
    created_at timestamp(6) NOT NULL,
    updated_at timestamp(6) NOT NULL,
    CONSTRAINT subtasks_task_id_fkey FOREIGN KEY (task_id) REFERENCES tasks(id)
);
