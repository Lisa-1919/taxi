CREATE TABLE IF NOT EXISTS public.rate
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    user_id bigint NOT NULL,
    user_type character varying(9) NOT NULL,
    ride_id bigint NOT NULL,
    rate double precision,
    ride_commentary text COLLATE pg_catalog."default",
    CONSTRAINT driver_pkey PRIMARY KEY (id)
)
GO
CREATE UNIQUE INDEX unique_rating ON public.rate (user_id, ride_id, user_type)
GO