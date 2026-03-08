package com.finflow.txp.transfer.application;

public interface BeneficiaryScreeningClient {

    PreCheckResult.BeneficiaryProfile getBeneficiaryProfile(String beneficiaryId);
}
