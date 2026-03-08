package com.finflow.txp.transfer.application;

import java.math.BigDecimal;

public interface FraudScreeningClient {

    PreCheckResult.RiskAssessment score(String tenantId, String sourceAccountId, String beneficiaryId, BigDecimal amount, String currency);
}
