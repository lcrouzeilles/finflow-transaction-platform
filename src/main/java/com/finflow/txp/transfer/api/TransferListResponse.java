package com.finflow.txp.transfer.api;

import java.util.List;

public record TransferListResponse(
        List<TransferResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
