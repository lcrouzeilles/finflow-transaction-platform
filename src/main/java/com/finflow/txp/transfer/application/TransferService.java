package com.finflow.txp.transfer.application;

import com.finflow.txp.outbox.application.OutboxService;
import com.finflow.txp.security.TenantContext;
import com.finflow.txp.transfer.api.CreateTransferRequest;
import com.finflow.txp.transfer.api.TransferResponse;
import com.finflow.txp.transfer.infrastructure.persistence.TransferEntity;
import com.finflow.txp.transfer.infrastructure.persistence.TransferRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final TransferPreCheckOrchestrator preCheckOrchestrator;
    private final OutboxService outboxService;
    private final TenantContext tenantContext;

    public TransferService(
            TransferRepository transferRepository,
            TransferMapper transferMapper,
            TransferPreCheckOrchestrator preCheckOrchestrator,
            OutboxService outboxService,
            TenantContext tenantContext) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
        this.preCheckOrchestrator = preCheckOrchestrator;
        this.outboxService = outboxService;
        this.tenantContext = tenantContext;
    }

    @Transactional
    public TransferResponse createTransfer(Authentication authentication, String idempotencyKey, @Valid CreateTransferRequest request) {
        String tenantId = tenantContext.tenantId(authentication);
        return transferRepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)
                .map(transferMapper::toResponse)
                .orElseGet(() -> createNewTransfer(tenantId, idempotencyKey, request));
    }

    private TransferResponse createNewTransfer(String tenantId, String idempotencyKey, CreateTransferRequest request) {
        PreCheckResult result = preCheckOrchestrator.evaluate(
                tenantId,
                request.sourceAccountId(),
                request.beneficiaryId(),
                request.amount(),
                request.currency());

        TransferEntity entity = TransferEntity.createNew(
                tenantId,
                idempotencyKey,
                request.clientReference(),
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.beneficiaryId(),
                request.amount(),
                request.currency(),
                result.riskAssessment().decision(),
                result.riskAssessment().reference());

        try {
            TransferEntity saved = transferRepository.save(entity);
            outboxService.enqueueTransferCreated(saved);
            return transferMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            return transferRepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)
                    .map(transferMapper::toResponse)
                    .orElseThrow(() -> ex);
        }
    }
}
