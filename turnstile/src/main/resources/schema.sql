CREATE TABLE IF NOT EXISTS turnstile_details
(
    id BIGSERIAL NOT NULL,
    username TEXT NOT NULL,
    zone_id TEXT NOT NULL,
    CONSTRAINT pk_turnstile_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS transit
(
    id BIGSERIAL NOT NULL,
    turnstile_username TEXT NOT NULL,
    username TEXT NOT NULL,
    ticket_id BIGSERIAL NOT NULL,
    validation_date TIMESTAMP NOT NULL,
    CONSTRAINT pk_transit_id PRIMARY KEY(id)
);
