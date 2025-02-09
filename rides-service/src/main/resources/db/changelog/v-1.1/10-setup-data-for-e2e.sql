DELETE FROM ride;
GO

INSERT INTO ride (id, driver_id, passenger_id, from_address, to_address, ride_status, order_date_time, cost)
VALUES
(101, '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'from', 'to', 'ACCEPTED', '2024-11-26 00:00:00', 4.00);
GO