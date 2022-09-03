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
    turnstileid BIGSERIAL NOT NULL,
    ticktid BIGSERIAL NOT NULL,
    validationDate TIMESTAMP NOT NULL,
    CONSTRAINT pk_transit_id PRIMARY KEY(id)
);
