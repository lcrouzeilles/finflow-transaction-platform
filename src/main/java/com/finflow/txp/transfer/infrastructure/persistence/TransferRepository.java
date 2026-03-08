package com.finflow.txp.transfer.infrastructure.persistence;

import com.finflow.txp.transfer.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {

    Optional<TransferEntity> findByTenantIdAndIdempotencyKey(String tenantId, String idempotencyKey);

    Optional<TransferEntity> findByTenantIdAndId(String tenantId, UUID id);

    Page<TransferEntity> findByTenantId(String tenantId, Pageable pageable);

    Page<TransferEntity> findByTenantIdAndStatusIn(String tenantId, Collection<TransferStatus> statuses, Pageable pageable);
}
