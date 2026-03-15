-- Insert mock data for subtask_statuses
INSERT INTO subtask_statuses (id, name) VALUES (1, 'Pending');
INSERT INTO subtask_statuses (id, name) VALUES (2, 'In Progress');
INSERT INTO subtask_statuses (id, name) VALUES (3, 'Completed');

-- Insert mock data for users
INSERT INTO users (id, email) VALUES (1, 'testuser@example.com');

-- Insert mock data for tasks
INSERT INTO tasks (id, name, user_id, created_at, updated_at) VALUES (1, 'Task 1 for Alex', 1, NOW(), NOW());
INSERT INTO tasks (id, name, user_id, created_at, updated_at) VALUES (2, 'Task 2 for Alex', 1, NOW(), NOW());
INSERT INTO tasks (id, name, user_id, created_at, updated_at) VALUES (3, 'Task 3 for Alex', 1, NOW(), NOW());

-- Insert mock data for subtasks
INSERT INTO subtasks (id, name, description, task_id, task_order, start_time, duration, estimated_time, completed, productive, is_tracked, is_ai_breakdown, status_id, created_at, updated_at)
VALUES (1, 'Subtask 1.1', 'Description for subtask 1.1', 1, 1, '2023-10-01 09:00:00', 60, 60, false, 80, true, false, 1, NOW(), NOW());

INSERT INTO subtasks (id, name, description, task_id, task_order, start_time, duration, estimated_time, completed, productive, is_tracked, is_ai_breakdown, status_id, created_at, updated_at)
VALUES (2, 'Subtask 1.2', 'Description for subtask 1.2', 1, 2, '2023-10-01 10:00:00', 45, 50, false, 90, true, false, 2, NOW(), NOW());

INSERT INTO subtasks (id, name, description, task_id, task_order, start_time, duration, estimated_time, completed, productive, is_tracked, is_ai_breakdown, status_id, created_at, updated_at)
VALUES (3, 'Subtask 2.1', 'Description for subtask 2.1', 2, 1, '2023-10-02 14:00:00', 30, 30, true, 100, true, false, 3, NOW(), NOW());

INSERT INTO subtasks (id, name, description, task_id, task_order, start_time, duration, estimated_time, completed, productive, is_tracked, is_ai_breakdown, status_id, created_at, updated_at)
VALUES (4, 'Subtask 3.1', 'Description for subtask 3.1', 3, 1, '2023-10-03 11:00:00', 90, 120, false, 70, true, true, 1, NOW(), NOW());

INSERT INTO subtasks (id, name, description, task_id, task_order, start_time, duration, estimated_time, completed, productive, is_tracked, is_ai_breakdown, status_id, created_at, updated_at)
VALUES (5, 'Subtask 3.2', 'Description for subtask 3.2', 3, 2, '2023-10-04 08:00:00', 120, 100, false, 85, false, false, 2, NOW(), NOW());

-- Reset sequences for auto-increment
ALTER TABLE subtask_statuses ALTER COLUMN id RESTART WITH 4;
ALTER TABLE users ALTER COLUMN id RESTART WITH 2;
ALTER TABLE tasks ALTER COLUMN id RESTART WITH 4;
ALTER TABLE subtasks ALTER COLUMN id RESTART WITH 6;
