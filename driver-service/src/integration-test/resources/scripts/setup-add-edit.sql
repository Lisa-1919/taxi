INSERT INTO driver (id, first_name, last_name, email, phone_number, sex, car_id, is_deleted)
VALUES
('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'testemail@example.com', '+1234567890', 'male', null, false),
('11111111-0000-1111-1111-111111111111', 'John', 'Doe', 'john@example.com', '+1234567892', 'male', null, false);


INSERT INTO car (id, license_plate, mark, colour, is_deleted)
VALUES (10, 'AB 1234-8', 'Toyota', 'Red', false);

update driver
set car_id = 10
where id = '11111111-1111-1111-1111-111111111111';
