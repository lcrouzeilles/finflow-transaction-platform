CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    client_reference VARCHAR(128) NOT NULL,
    source_account_id VARCHAR(64) NOT NULL,
    destination_account_id VARCHAR(64) NOT NULL,
    beneficiary_id VARCHAR(64) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    risk_decision VARCHAR(32) NOT NULL,
    risk_reference VARCHAR(128),
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_transfers_tenant_idempotency UNIQUE (tenant_id, idempotency_key),
    CONSTRAINT uk_transfers_tenant_client_reference UNIQUE (tenant_id, client_reference)
);

CREATE INDEX idx_transfers_tenant_status_created ON transfers (tenant_id, status, created_at);
CREATE INDEX idx_transfers_status_created ON transfers (status, created_at);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(128) NOT NULL,
    topic VARCHAR(128) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    attempts INTEGER NOT NULL,
    last_error VARCHAR(512),
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    next_attempt_at TIMESTAMP NOT NULL,
    version BIGINT
);

CREATE INDEX idx_outbox_status_next_attempt ON outbox_events (status, next_attempt_at);
CREATE INDEX idx_outbox_aggregate ON outbox_events (aggregate_type, aggregate_id);
