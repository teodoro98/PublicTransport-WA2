CREATE TABLE IF NOT EXISTS transaction (
                                      id BIGSERIAL NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      order_id BIGINT NOT NULL,
                                      credit_card_number varchar(255) not null,
                                      card_holder varchar(255) not null,
                                      CONSTRAINT pk_transaction_id PRIMARY KEY(id)
);