CREATE TABLE IF NOT EXISTS transit
(
    id BIGSERIAL NOT NULL,
    turnstileid BIGSERIAL NOT NULL,
    ticktid BIGSERIAL NOT NULL,
    validationDate TIMESTAMP NOT NULL,
    CONSTRAINT pk_ticket_id PRIMARY KEY(id)
)