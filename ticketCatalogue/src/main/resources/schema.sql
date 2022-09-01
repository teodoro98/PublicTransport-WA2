

CREATE TABLE IF NOT EXISTS ticket (
                                      id BIGSERIAL NOT NULL ,
                                      price double precision NOT NULL,
                                      zone TEXT NOT NULL,
                                      type TEXT NOT NULL,
                                      validitytime TIMESTAMP,
                                      maxnumber_of_rides INT,
                                      CONSTRAINT pk_ticket_id PRIMARY KEY(id)

);


CREATE TABLE IF NOT EXISTS order_order (
                                      id BIGSERIAL NOT NULL ,
                                      quantity INT NOT NULL,
                                      price double precision NOT NULL,
                                      status varchar(255) NOT NULL,
                                      buyer_id BIGINT NOT NULL,
                                      type_id BIGINT NOT NULL references ticket(id),
                                      CONSTRAINT pk_order_order_id PRIMARY KEY(id)
);