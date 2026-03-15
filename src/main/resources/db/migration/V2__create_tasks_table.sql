CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    user_id int NOT NULL,
    created_at timestamp(6) NOT NULL,
    updated_at timestamp(6) NOT NULL,
    CONSTRAINT tasks_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);
