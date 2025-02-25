DELETE FROM passenger;
GO

INSERT INTO passenger (id, first_name, last_name, email, phone_number, is_deleted)
VALUES
('ed20a11d-9717-4ac6-b14a-119a2c7b9634', 'test', 'test', 'test-edit-passenger@gmail.com', '+999-00-000-00-99', false),
('dc6ea845-03a1-4c74-9fbf-4bf6e4198578', 'test-passenger', 'test-passenger', 'test-passenger@gmail.com', '+375 11 111 11 22', false),
('48d48295-cf63-4e56-8088-7faed8057900', 'test', 'test', 'test-delete-passenger@test.com', '+000 00 000 00 11', false);
GO