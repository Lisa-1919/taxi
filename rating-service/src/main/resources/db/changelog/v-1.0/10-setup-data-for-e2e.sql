DELETE FROM rate;
GO

INSERT INTO rate (id, user_id, user_type, ride_id, rate, ride_commentary)
VALUES
(100, 100, 'PASSENGER', 100, 5, 'Comment');
GO