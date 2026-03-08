package com.finflow.txp.transfer.application;

import com.finflow.txp.common.exception.ResourceNotFoundException;
import com.finflow.txp.security.TenantContext;
import com.finflow.txp.transfer.api.TransferListResponse;
import com.finflow.txp.transfer.api.TransferResponse;
import com.finflow.txp.transfer.domain.TransferStatus;
import com.finflow.txp.transfer.infrastructure.persistence.TransferEntity;
import com.finflow.txp.transfer.infrastructure.persistence.TransferRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TransferQueryService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final TenantContext tenantContext;

    public TransferQueryService(TransferRepository transferRepository, TransferMapper transferMapper, TenantContext tenantContext) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
        this.tenantContext = tenantContext;
    }

    public TransferResponse getTransfer(Authentication authentication, UUID transferId) {
        String tenantId = tenantContext.tenantId(authentication);
        TransferEntity entity = transferRepository.findByTenantIdAndId(tenantId, transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        return transferMapper.toResponse(entity);
    }

    public TransferListResponse listTransfers(Authentication authentication, String status, Pageable pageable) {
        String tenantId = tenantContext.tenantId(authentication);
        Page<TransferEntity> page = status == null || status.isBlank()
                ? transferRepository.findByTenantId(tenantId, pageable)
                : transferRepository.findByTenantIdAndStatusIn(tenantId, List.of(TransferStatus.valueOf(status)), pageable);

        return new TransferListResponse(
                page.map(transferMapper::toResponse).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
