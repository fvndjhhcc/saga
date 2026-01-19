package by.raikiri.order_creation_service.dto;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        int iteration
) {}
