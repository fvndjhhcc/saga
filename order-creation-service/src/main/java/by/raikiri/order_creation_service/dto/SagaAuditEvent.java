package by.raikiri.order_creation_service.dto;

import java.time.Instant;
import java.util.UUID;

public record SagaAuditEvent(
        UUID orderId,
        String stage,   // CREATION | CONFIRMATION | PROCESSING
        String status,  // STARTED | COMPLETED | FAILED | ROLLED_BACK
        Instant timestamp
) {}
