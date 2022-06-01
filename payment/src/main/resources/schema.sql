CREATE TABLE IF NOT EXISTS transaction (
                                      transaction_id BIGSERIAL PRIMARY KEY,
                                      type TEXT NOT NULL,
                                      status varchar(255) NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      order_id BIGINT NOT NULL
);