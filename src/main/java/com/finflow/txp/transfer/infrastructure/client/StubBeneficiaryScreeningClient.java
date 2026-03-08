package com.finflow.txp.transfer.infrastructure.client;

import com.finflow.txp.transfer.application.BeneficiaryScreeningClient;
import com.finflow.txp.transfer.application.PreCheckResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class StubBeneficiaryScreeningClient implements BeneficiaryScreeningClient {

    @Override
    @Cacheable(cacheNames = "beneficiaries", key = "#beneficiaryId")
    public PreCheckResult.BeneficiaryProfile getBeneficiaryProfile(String beneficiaryId) {
        return new PreCheckResult.BeneficiaryProfile(beneficiaryId, true);
    }
}
