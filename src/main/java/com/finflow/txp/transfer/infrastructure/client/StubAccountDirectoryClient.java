package com.finflow.txp.transfer.infrastructure.client;

import com.finflow.txp.transfer.application.AccountDirectoryClient;
import com.finflow.txp.transfer.application.PreCheckResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class StubAccountDirectoryClient implements AccountDirectoryClient {

    @Override
    @Cacheable(cacheNames = "accountProfiles", key = "#accountId")
    public PreCheckResult.AccountProfile getAccountProfile(String accountId) {
        return new PreCheckResult.AccountProfile(accountId, true, "USD");
    }
}
