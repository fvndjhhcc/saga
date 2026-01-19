package by.raikiri.order_creation_service.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record SagaAuditResponse(
        UUID orderId,
        String stage,
        String status,
        Instant timestamp
) {}
