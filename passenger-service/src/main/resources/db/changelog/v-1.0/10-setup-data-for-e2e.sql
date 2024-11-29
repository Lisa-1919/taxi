DELETE FROM passenger;
GO

INSERT INTO passenger (id, first_name, last_name, email, phone_number, is_deleted)
VALUES
(100, 'John', 'Doe', 'testemail@example.com', '+1234567890', false),
(101, 'Kira', 'Doe', 'kira@example.com', '+1230067890', false);
GO