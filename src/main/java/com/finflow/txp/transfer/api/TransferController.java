package com.finflow.txp.transfer.api;

import com.finflow.txp.transfer.application.TransferQueryService;
import com.finflow.txp.transfer.application.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;
    private final TransferQueryService transferQueryService;

    public TransferController(TransferService transferService, TransferQueryService transferQueryService) {
        this.transferService = transferService;
        this.transferQueryService = transferQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a transfer", description = "Creates an idempotent transaction request and emits an outbox event.")
    public TransferResponse createTransfer(
            Authentication authentication,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request) {
        return transferService.createTransfer(authentication, idempotencyKey, request);
    }

    @GetMapping("/{transferId}")
    @Operation(summary = "Get transfer by id")
    public TransferResponse getTransfer(
            Authentication authentication,
            @PathVariable UUID transferId) {
        return transferQueryService.getTransfer(authentication, transferId);
    }

    @GetMapping
    @Operation(summary = "List transfers with pagination")
    public TransferListResponse listTransfers(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @Parameter(description = "Spring pageable parameters: page, size, sort")
            @ParameterObject Pageable pageable) {
        return transferQueryService.listTransfers(authentication, status, pageable);
    }
}
