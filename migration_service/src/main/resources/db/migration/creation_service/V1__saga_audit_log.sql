CREATE TABLE saga_audit_log (
                                id BIGSERIAL PRIMARY KEY,

                                order_id UUID NOT NULL,
                                stage VARCHAR(32) NOT NULL,
                                status VARCHAR(32) NOT NULL,

                                timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_saga_audit_order
    ON saga_audit_log (order_id);

CREATE INDEX idx_saga_audit_timestamp
    ON saga_audit_log (timestamp);
