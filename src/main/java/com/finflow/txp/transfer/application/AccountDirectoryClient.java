package com.finflow.txp.transfer.application;

public interface AccountDirectoryClient {

    PreCheckResult.AccountProfile getAccountProfile(String accountId);
}
