package com.finflow.txp.transfer.application;

import com.finflow.txp.transfer.domain.RiskDecision;

public record PreCheckResult(
        AccountProfile accountProfile,
        BeneficiaryProfile beneficiaryProfile,
        RiskAssessment riskAssessment
) {
    public record AccountProfile(String accountId, boolean active, String currency) {}
    public record BeneficiaryProfile(String beneficiaryId, boolean allowed) {}
    public record RiskAssessment(RiskDecision decision, String reference) {}
}
