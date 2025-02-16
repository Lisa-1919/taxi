DELETE FROM rate;
GO

INSERT INTO rate (id, user_id, user_type, ride_id, rate, ride_commentary)
VALUES
(100, '11111111-1111-1111-1111-111111111111', 'PASSENGER', 100, 5, 'Comment');
GO