package com.finflow.txp.transfer.infrastructure.client;

import com.finflow.txp.transfer.application.FraudScreeningClient;
import com.finflow.txp.transfer.application.PreCheckResult;
import com.finflow.txp.transfer.domain.RiskDecision;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StubFraudScreeningClient implements FraudScreeningClient {

    @Override
    @Retry(name = "fraudService")
    @CircuitBreaker(name = "fraudService", fallbackMethod = "fallback")
    @Bulkhead(name = "fraudService")
    public PreCheckResult.RiskAssessment score(String tenantId, String sourceAccountId, String beneficiaryId, BigDecimal amount, String currency) {
        boolean highRisk = amount.compareTo(new BigDecimal("10000")) > 0;
        RiskDecision decision = highRisk ? RiskDecision.MANUAL_REVIEW : RiskDecision.APPROVED;
        return new PreCheckResult.RiskAssessment(decision, "risk-" + Math.abs((tenantId + beneficiaryId).hashCode()));
    }

    public PreCheckResult.RiskAssessment fallback(String tenantId, String sourceAccountId, String beneficiaryId, BigDecimal amount, String currency, Throwable throwable) {
        return new PreCheckResult.RiskAssessment(RiskDecision.MANUAL_REVIEW, "risk-fallback");
    }
}
