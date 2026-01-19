CREATE TABLE created_orders (
                        id UUID PRIMARY KEY,
                        status VARCHAR(32) NOT NULL,
                        iteration INT NOT NULL,
                        created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE outbox_event (
                              id UUID PRIMARY KEY,
                              aggregate_id UUID NOT NULL,
                              event_type VARCHAR(50) NOT NULL,
                              payload JSONB NOT NULL,
                              created_at TIMESTAMP DEFAULT now()
);


