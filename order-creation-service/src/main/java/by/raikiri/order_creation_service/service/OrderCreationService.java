package by.raikiri.order_creation_service.service;

import by.raikiri.order_creation_service.config.OutboxEventSerializer;
import by.raikiri.order_creation_service.dto.OrderCreatedEvent;
import by.raikiri.order_creation_service.dto.OrderDTO;
import by.raikiri.order_creation_service.dto.OrderFailedEvent;
import by.raikiri.order_creation_service.dto.SagaAuditEvent;
import by.raikiri.order_creation_service.model.Order;
import by.raikiri.order_creation_service.model.OutboxEvent;
import by.raikiri.order_creation_service.repository.OrderRepository;
import by.raikiri.order_creation_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final OutboxEventSerializer serializer;

    private final AtomicInteger counter = new AtomicInteger();

    @Transactional
    public OrderDTO createOrder() {
        int iteration = counter.incrementAndGet();
        UUID orderId = UUID.randomUUID();

        audit(orderId, "CREATION", "STARTED");

        if (iteration % 3 == 0) {
            orderRepository.save(new Order(orderId, "failed", iteration));

            outboxRepository.save(
                    OutboxEvent.createEvent(
                            orderId,
                            "order.failed",
                            serializer.serialize(
                                    new OrderFailedEvent(
                                            orderId,
                                            "CREATION",
                                            "Creation failed"
                                    )
                            )
                    )
            );

            audit(orderId, "CREATION", "FAILED");

            log.warn("Order {} FAILED at CREATION", orderId);
            return new OrderDTO(orderId, "failed", iteration);
        }

        orderRepository.save(new Order(orderId, "created", iteration));

        outboxRepository.save(
                OutboxEvent.createEvent(
                        orderId,
                        "order.created",
                        serializer.serialize(
                                new OrderCreatedEvent(orderId, iteration)
                        )
                )
        );

        audit(orderId, "CREATION", "COMPLETED");

        log.info("Order {} CREATED", orderId);
        return new OrderDTO(orderId, "created", iteration);
    }

    @KafkaListener(topics = "order.failed", groupId = "order-creation-group")
    @Transactional
    public void rollback(String message) throws Exception {
        OrderFailedEvent event = unwrap(message, OrderFailedEvent.class);

        orderRepository.deleteById(event.orderId());

        audit(event.orderId(), "CREATION", "ROLLED_BACK");

        log.info("Creation rolled back for {}", event.orderId());
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


