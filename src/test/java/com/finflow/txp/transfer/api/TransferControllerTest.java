package com.finflow.txp.transfer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finflow.txp.config.TracingSupport;
import com.finflow.txp.transfer.application.TransferQueryService;
import com.finflow.txp.transfer.application.TransferService;
import com.finflow.txp.transfer.domain.RiskDecision;
import com.finflow.txp.transfer.domain.TransferStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TransferController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;
    @MockBean
    private TransferQueryService transferQueryService;
    @MockBean
    private TracingSupport tracingSupport;

    @Test
    void shouldCreateTransfer() throws Exception {
        UUID transferId = UUID.randomUUID();
        when(transferService.createTransfer(any(), eq("idem-1"), any(CreateTransferRequest.class)))
                .thenReturn(new TransferResponse(
                        transferId,
                        "client-ref-1",
                        "acct-1",
                        "acct-2",
                        new BigDecimal("125.50"),
                        "USD",
                        TransferStatus.PENDING_REVIEW,
                        RiskDecision.APPROVED,
                        Instant.parse("2026-03-07T12:00:00Z"),
                        Instant.parse("2026-03-07T12:00:00Z")
                ));

        CreateTransferRequest request = new CreateTransferRequest(
                "client-ref-1",
                "acct-1",
                "acct-2",
                new BigDecimal("125.50"),
                "USD",
                "beneficiary-1");

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "idem-1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(transferId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.currency").value("USD"));
    }
}
