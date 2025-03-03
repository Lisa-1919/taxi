DELETE FROM rate;
GO

INSERT INTO rate (id, user_id, user_type, ride_id, rate, ride_commentary)
VALUES
(100, 'dc6ea845-03a1-4c74-9fbf-4bf6e4198578', 'PASSENGER', 100, 5, 'Comment');
GO