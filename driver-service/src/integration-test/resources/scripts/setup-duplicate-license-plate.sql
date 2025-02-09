INSERT INTO car (id, license_plate, mark, colour, is_deleted)
VALUES
(10, 'AB 1234-7', 'Toyota', 'Red', false),
(11, 'AB 1234-10', 'Toyota', 'Red', false);

INSERT INTO driver (id, first_name, last_name, email, phone_number, sex, car_id, is_deleted)
VALUES ('11111111-1111-1111-1111-111111111111', 'Jane', 'Smith', 'jane@example.com', '+1234567891', 'female', 10, false);
VALUES ('11111111-0000-1111-1111-111111111111', 'John', 'Doe', 'john@example.com', '+1234567892', 'male', 11, false);
