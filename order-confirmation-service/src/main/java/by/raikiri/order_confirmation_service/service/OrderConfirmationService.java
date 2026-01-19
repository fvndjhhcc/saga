package by.raikiri.order_confirmation_service.service;

import by.raikiri.order_confirmation_service.config.OutboxEventSerializer;
import by.raikiri.order_confirmation_service.dto.OrderConfirmedEvent;
import by.raikiri.order_confirmation_service.dto.OrderCreatedEvent;
import by.raikiri.order_confirmation_service.dto.OrderFailedEvent;
import by.raikiri.order_confirmation_service.dto.SagaAuditEvent;
import by.raikiri.order_confirmation_service.model.ConfirmedOrder;
import by.raikiri.order_confirmation_service.model.OutboxEvent;
import by.raikiri.order_confirmation_service.repository.ConfirmedOrderRepository;
import by.raikiri.order_confirmation_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmationService {

    private final ConfirmedOrderRepository repository;
    private final OutboxRepository outboxRepository;
    private final OutboxEventSerializer serializer;

    @KafkaListener(topics = "order.created", groupId = "order-confirmation-group")
    @Transactional
    public void confirm(String message) throws Exception {

        OrderCreatedEvent event = unwrap(message, OrderCreatedEvent.class);
        UUID orderId = event.orderId();
        int iteration = event.iteration();

        audit(orderId, "CONFIRMATION", "STARTED");

        log.info("Confirming order {}", orderId);

        if (iteration % 5 == 0) {
            repository.save(new ConfirmedOrder(orderId, "failed", iteration));

            outboxRepository.save(
                    OutboxEvent.createEvent(
                            orderId,
                            "order.failed",
                            serializer.serialize(
                                    new OrderFailedEvent(
                                            orderId,
                                            "CONFIRMATION",
                                            "Confirmation failed"
                                    )
                            )
                    )
            );

            audit(orderId, "CONFIRMATION", "FAILED");

            log.warn("Order {} FAILED at CONFIRMATION", orderId);
            return;
        }

        repository.save(new ConfirmedOrder(orderId, "confirmed", iteration));

        outboxRepository.save(
                OutboxEvent.createEvent(
                        orderId,
                        "order.confirmed",
                        serializer.serialize(
                                new OrderConfirmedEvent(orderId, iteration)
                        )
                )
        );

        audit(orderId, "CONFIRMATION", "COMPLETED");

        log.info("Order {} CONFIRMED", orderId);
    }

    @KafkaListener(topics = "order.failed", groupId = "order-confirmation-group")
    @Transactional
    public void rollback(String message) throws Exception {
        OrderFailedEvent event = unwrap(message, OrderFailedEvent.class);

        repository.deleteById(event.orderId());

        audit(event.orderId(), "CONFIRMATION", "ROLLED_BACK");

        log.info("Confirmation rolled back for {}", event.orderId());
    }

    private void audit(UUID orderId, String stage, String status) {
        outboxRepository.save(
                OutboxEvent.createEvent(
                        orderId,
                        "order.saga.audit",
                        serializer.serialize(
                                new SagaAuditEvent(
                                        orderId,
                                        stage,
                                        status,
                                        Instant.now()
                                )
                        )
                )
        );
    }

    private <T> T unwrap(String message, Class<T> type) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(mapper.readValue(message, String.class), type);
    }
}
