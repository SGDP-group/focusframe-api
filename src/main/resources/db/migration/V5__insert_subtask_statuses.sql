INSERT INTO subtask_statuses (id, name) VALUES
    (1, 'Planned'),
    (2, 'Ongoing'),
    (3, 'Completed'),
    (4, 'Deleted'),
    (5, 'Rescheduled')
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('subtask_statuses', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM subtask_statuses),
    true
);
