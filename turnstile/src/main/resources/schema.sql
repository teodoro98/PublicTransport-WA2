CREATE TABLE IF NOT EXISTS transit (
                                      id BIGSERIAL NOT NULL ,
                                      turnstileId NUMBER NOT NULL,
                                      ticktId NUMBER NOT NULL,
                                      validationDate TIMESTAMP NOT NULL
                                      CONSTRAINT pk_ticket_id PRIMARY KEY(id)