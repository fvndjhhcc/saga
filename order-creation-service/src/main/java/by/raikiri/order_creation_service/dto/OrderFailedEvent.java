package by.raikiri.order_creation_service.dto;

import java.util.UUID;

public record OrderFailedEvent(
        UUID orderId,
        String failedStage,
        String reason
) {}
