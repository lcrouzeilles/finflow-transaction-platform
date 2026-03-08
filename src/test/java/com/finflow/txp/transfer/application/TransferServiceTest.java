package com.finflow.txp.transfer.application;

import com.finflow.txp.outbox.application.OutboxService;
import com.finflow.txp.security.TenantContext;
import com.finflow.txp.transfer.api.CreateTransferRequest;
import com.finflow.txp.transfer.api.TransferResponse;
import com.finflow.txp.transfer.domain.RiskDecision;
import com.finflow.txp.transfer.infrastructure.persistence.TransferEntity;
import com.finflow.txp.transfer.infrastructure.persistence.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private TransferMapper transferMapper;
    @Mock
    private TransferPreCheckOrchestrator preCheckOrchestrator;
    @Mock
    private OutboxService outboxService;
    @Mock
    private TenantContext tenantContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransferService transferService;

    private CreateTransferRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTransferRequest(
                "client-ref-1",
                "acct-1",
                "acct-2",
                new BigDecimal("125.50"),
                "USD",
                "beneficiary-1");
    }

    @Test
    void shouldCreateTransferAndEnqueueOutboxEvent() {
        when(tenantContext.tenantId(authentication)).thenReturn("tenant-a");
        when(transferRepository.findByTenantIdAndIdempotencyKey("tenant-a", "idem-1")).thenReturn(Optional.empty());
        when(preCheckOrchestrator.evaluate("tenant-a", "acct-1", "beneficiary-1", new BigDecimal("125.50"), "USD"))
                .thenReturn(new PreCheckResult(
                        new PreCheckResult.AccountProfile("acct-1", true, "USD"),
                        new PreCheckResult.BeneficiaryProfile("beneficiary-1", true),
                        new PreCheckResult.RiskAssessment(RiskDecision.APPROVED, "risk-1")));

        when(transferRepository.save(any(TransferEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferMapper.toResponse(any(TransferEntity.class))).thenAnswer(invocation -> {
            TransferEntity entity = invocation.getArgument(0);
            return new TransferResponse(
                    entity.getId(),
                    entity.getClientReference(),
                    entity.getSourceAccountId(),
                    entity.getDestinationAccountId(),
                    entity.getAmount(),
                    entity.getCurrency(),
                    entity.getStatus(),
                    entity.getRiskDecision(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt());
        });

        TransferResponse response = transferService.createTransfer(authentication, "idem-1", request);

        assertThat(response.transferId()).isNotNull();
        assertThat(response.clientReference()).isEqualTo("client-ref-1");

        ArgumentCaptor<TransferEntity> entityCaptor = ArgumentCaptor.forClass(TransferEntity.class);
        verify(transferRepository).save(entityCaptor.capture());
        verify(outboxService).enqueueTransferCreated(entityCaptor.getValue());
        assertThat(entityCaptor.getValue().getTenantId()).isEqualTo("tenant-a");
        assertThat(entityCaptor.getValue().getRiskDecision()).isEqualTo(RiskDecision.APPROVED);
    }

    @Test
    void shouldReturnExistingTransferOnIdempotentReplay() {
        TransferEntity existing = TransferEntity.createNew(
                "tenant-a", "idem-1", "client-ref-1", "acct-1", "acct-2", "beneficiary-1",
                new BigDecimal("125.50"), "USD", RiskDecision.APPROVED, "risk-1");

        TransferResponse mapped = new TransferResponse(
                existing.getId(),
                existing.getClientReference(),
                existing.getSourceAccountId(),
                existing.getDestinationAccountId(),
                existing.getAmount(),
                existing.getCurrency(),
                existing.getStatus(),
                existing.getRiskDecision(),
                existing.getCreatedAt(),
                existing.getUpdatedAt());

        when(tenantContext.tenantId(authentication)).thenReturn("tenant-a");
        when(transferRepository.findByTenantIdAndIdempotencyKey("tenant-a", "idem-1")).thenReturn(Optional.of(existing));
        when(transferMapper.toResponse(existing)).thenReturn(mapped);

        TransferResponse response = transferService.createTransfer(authentication, "idem-1", request);

        assertThat(response.transferId()).isEqualTo(existing.getId());
        verifyNoInteractions(preCheckOrchestrator, outboxService);
        verify(transferRepository, never()).save(any());
    }
}
