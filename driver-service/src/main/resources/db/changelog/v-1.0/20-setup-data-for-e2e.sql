DELETE FROM driver;
GO
DELETE FROM car;
GO

INSERT INTO car (id, license_plate, mark, colour, is_deleted)
VALUES
(100, 'AB 1111-8', 'mark', 'yellow', false),
(101, 'AB 0000-8', 'Toyota', 'Red', false);
GO

INSERT INTO driver (id, first_name, last_name, email, phone_number, sex, car_id, is_deleted)
VALUES
(100, 'John', 'Doe', 'testemail@example.com', '+1234567890', 'male', null, false),
(101, 'John', 'Doe', 'john@example.com', '+1234567892', 'male', 101, false),
(102, 'Dan', 'Li', 'dan@example.com', '+1234567111', 'male', null, false),
(103, 'Jake', 'Peralta', 'jake@example.com', '+1234567911', 'male', null, false);
GO
