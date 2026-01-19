package by.raikiri.order_confirmation_service.dto;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        int iteration
) {}
