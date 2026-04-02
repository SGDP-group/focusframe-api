CREATE TABLE doormount_led_states (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    session_id VARCHAR(128),
    green_locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_status_id INT,
    signal_type VARCHAR(32) NOT NULL DEFAULT 'idle',
    red INT NOT NULL DEFAULT 0 CHECK (red BETWEEN 0 AND 255),
    green INT NOT NULL DEFAULT 0 CHECK (green BETWEEN 0 AND 255),
    blue INT NOT NULL DEFAULT 0 CHECK (blue BETWEEN 0 AND 255),
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    CONSTRAINT doormount_led_states_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);
