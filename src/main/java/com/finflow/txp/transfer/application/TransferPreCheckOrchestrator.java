package com.finflow.txp.transfer.application;

import com.finflow.txp.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
public class TransferPreCheckOrchestrator {

    private final ExecutorService virtualThreadExecutor;
    private final AccountDirectoryClient accountDirectoryClient;
    private final BeneficiaryScreeningClient beneficiaryScreeningClient;
    private final FraudScreeningClient fraudScreeningClient;

    public TransferPreCheckOrchestrator(
            ExecutorService virtualThreadExecutor,
            AccountDirectoryClient accountDirectoryClient,
            BeneficiaryScreeningClient beneficiaryScreeningClient,
            FraudScreeningClient fraudScreeningClient) {
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.accountDirectoryClient = accountDirectoryClient;
        this.beneficiaryScreeningClient = beneficiaryScreeningClient;
        this.fraudScreeningClient = fraudScreeningClient;
    }

    public PreCheckResult evaluate(String tenantId, String sourceAccountId, String beneficiaryId, BigDecimal amount, String currency) {
        CompletableFuture<PreCheckResult.AccountProfile> accountFuture =
                CompletableFuture.supplyAsync(() -> accountDirectoryClient.getAccountProfile(sourceAccountId), virtualThreadExecutor);
        CompletableFuture<PreCheckResult.BeneficiaryProfile> beneficiaryFuture =
                CompletableFuture.supplyAsync(() -> beneficiaryScreeningClient.getBeneficiaryProfile(beneficiaryId), virtualThreadExecutor);
        CompletableFuture<PreCheckResult.RiskAssessment> riskFuture =
                CompletableFuture.supplyAsync(() -> fraudScreeningClient.score(tenantId, sourceAccountId, beneficiaryId, amount, currency), virtualThreadExecutor);

        PreCheckResult result = accountFuture.thenCombine(beneficiaryFuture, Intermediate::new)
                .thenCombine(riskFuture, (intermediate, riskAssessment) ->
                        new PreCheckResult(intermediate.accountProfile(), intermediate.beneficiaryProfile(), riskAssessment))
                .join();

        validate(result, currency);
        return result;
    }

    private void validate(PreCheckResult result, String currency) {
        if (!result.accountProfile().active()) {
            throw new BusinessRuleException("Source account is inactive");
        }
        if (!currency.equals(result.accountProfile().currency())) {
            throw new BusinessRuleException("Currency mismatch for source account");
        }
        if (!result.beneficiaryProfile().allowed()) {
            throw new BusinessRuleException("Beneficiary is blocked");
        }
    }

    private record Intermediate(
            PreCheckResult.AccountProfile accountProfile,
            PreCheckResult.BeneficiaryProfile beneficiaryProfile
    ) {
    }
}
