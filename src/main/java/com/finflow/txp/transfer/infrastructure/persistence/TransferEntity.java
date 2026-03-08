package com.finflow.txp.transfer.infrastructure.persistence;

import com.finflow.txp.transfer.domain.RiskDecision;
import com.finflow.txp.transfer.domain.TransferStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_transfers_tenant_idempotency", columnNames = {"tenant_id", "idempotency_key"}),
                @UniqueConstraint(name = "uk_transfers_tenant_client_reference", columnNames = {"tenant_id", "client_reference"})
        },
        indexes = {
                @Index(name = "idx_transfers_tenant_status_created", columnList = "tenant_id,status,created_at"),
                @Index(name = "idx_transfers_status_created", columnList = "status,created_at")
        })
public class TransferEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @Column(name = "client_reference", nullable = false, length = 128)
    private String clientReference;

    @Column(name = "source_account_id", nullable = false, length = 64)
    private String sourceAccountId;

    @Column(name = "destination_account_id", nullable = false, length = 64)
    private String destinationAccountId;

    @Column(name = "beneficiary_id", nullable = false, length = 64)
    private String beneficiaryId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransferStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_decision", nullable = false, length = 32)
    private RiskDecision riskDecision;

    @Column(name = "risk_reference", length = 128)
    private String riskReference;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static TransferEntity createNew(
            String tenantId,
            String idempotencyKey,
            String clientReference,
            String sourceAccountId,
            String destinationAccountId,
            String beneficiaryId,
            BigDecimal amount,
            String currency,
            RiskDecision riskDecision,
            String riskReference) {
        TransferEntity entity = new TransferEntity();
        entity.id = UUID.randomUUID();
        entity.tenantId = tenantId;
        entity.idempotencyKey = idempotencyKey;
        entity.clientReference = clientReference;
        entity.sourceAccountId = sourceAccountId;
        entity.destinationAccountId = destinationAccountId;
        entity.beneficiaryId = beneficiaryId;
        entity.amount = amount;
        entity.currency = currency;
        entity.riskDecision = riskDecision;
        entity.riskReference = riskReference;
        entity.status = riskDecision == RiskDecision.REJECTED ? TransferStatus.REJECTED : TransferStatus.PENDING_REVIEW;
        entity.createdAt = Instant.now();
        entity.updatedAt = entity.createdAt;
        return entity;
    }

    public void markAccepted() {
        this.status = TransferStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void markRejected() {
        this.status = TransferStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getClientReference() {
        return clientReference;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public RiskDecision getRiskDecision() {
        return riskDecision;
    }

    public String getRiskReference() {
        return riskReference;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
