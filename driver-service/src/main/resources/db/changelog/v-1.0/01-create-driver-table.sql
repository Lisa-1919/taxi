create TABLE IF NOT EXISTS public.driver
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    car_id bigint,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    email character varying(255) NOT NULL UNIQUE,
    phone_number character varying(255) NOT NULL UNIQUE,
    sex character varying(255) NOT NULL,
    is_deleted boolean,
    CONSTRAINT driver_pkey PRIMARY KEY (id),
    CONSTRAINT driver_email_key UNIQUE (email),
    CONSTRAINT driver_phone_number_key UNIQUE (phone_number)
)
GO