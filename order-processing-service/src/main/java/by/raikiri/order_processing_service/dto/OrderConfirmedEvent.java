package by.raikiri.order_processing_service.dto;

import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId,
        int iteration
) {}
