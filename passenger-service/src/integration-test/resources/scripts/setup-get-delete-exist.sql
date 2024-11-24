-- Добавляем одного активного пассажира
INSERT INTO passenger (id, first_name, last_name, email, phone_number, is_deleted)
VALUES
    (10, 'John', 'Doe', 'john.doe@example.com', '+1234567890', false),
    (11, 'Jane', 'Smith', 'jane.smith@example.com', '+1234567891', true);
