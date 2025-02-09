DELETE FROM passenger;
GO

INSERT INTO passenger (id, first_name, last_name, email, phone_number, is_deleted)
VALUES
('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'testemail@example.com', '+1234567890', false),
('11111111-0000-1111-1111-111111111111', 'Kira', 'Doe', 'kira@example.com', '+1230067890', false);
GO