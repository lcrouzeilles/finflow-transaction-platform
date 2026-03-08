package com.finflow.txp.transfer.infrastructure.persistence;

import com.finflow.txp.transfer.domain.RiskDecision;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TransferRepositoryTest {

    @Autowired
    private TransferRepository transferRepository;

    @Test
    void shouldFindByTenantAndIdempotencyKey() {
        TransferEntity entity = TransferEntity.createNew(
                "tenant-a",
                "idem-1",
                "client-ref-1",
                "acct-1",
                "acct-2",
                "beneficiary-1",
                new BigDecimal("10.00"),
                "USD",
                RiskDecision.APPROVED,
                "risk-1");

        transferRepository.save(entity);

        assertThat(transferRepository.findByTenantIdAndIdempotencyKey("tenant-a", "idem-1"))
                .isPresent()
                .get()
                .extracting(TransferEntity::getClientReference)
                .isEqualTo("client-ref-1");
    }
}
