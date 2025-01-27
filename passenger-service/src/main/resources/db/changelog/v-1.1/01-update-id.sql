CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE passenger ALTER COLUMN id DROP IDENTITY;

ALTER TABLE passenger ALTER COLUMN id TYPE UUID USING gen_random_uuid();

ALTER TABLE passenger ALTER COLUMN id SET DEFAULT gen_random_uuid();
