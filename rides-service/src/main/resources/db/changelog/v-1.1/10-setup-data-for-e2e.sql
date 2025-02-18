DELETE FROM ride;
GO

INSERT INTO ride (id, driver_id, passenger_id, from_address, to_address, ride_status, order_date_time, cost)
VALUES
(101, '4ef35903-b7dc-4865-a893-d115c4a303e5', 'dc6ea845-03a1-4c74-9fbf-4bf6e4198578', 'from', 'to', 'ACCEPTED', '2024-11-26 00:00:00', 4.00);
GO