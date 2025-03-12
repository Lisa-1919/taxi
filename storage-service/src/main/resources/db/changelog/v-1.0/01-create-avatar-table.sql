create TABLE IF NOT EXISTS public.avatar
(
    user_id uuid NOT NULL PRIMARY KEY,
    filename text UNIQUE NOT NULL
)
GO