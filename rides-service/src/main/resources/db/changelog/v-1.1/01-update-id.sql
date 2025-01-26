CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE ride ALTER COLUMN driver_id TYPE UUID USING gen_random_uuid();
GO

ALTER TABLE ride ALTER COLUMN passenger_id TYPE UUID USING gen_random_uuid();
GO