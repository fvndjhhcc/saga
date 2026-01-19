package by.raikiri.order_processing_service.service;

import by.raikiri.order_processing_service.config.OutboxEventSerializer;
import by.raikiri.order_processing_service.dto.OrderConfirmedEvent;
import by.raikiri.order_processing_service.dto.OrderFailedEvent;
import by.raikiri.order_processing_service.dto.SagaAuditEvent;
import by.raikiri.order_processing_service.model.ProcessedOrder;
import by.raikiri.order_processing_service.model.OutboxEvent;
import by.raikiri.order_processing_service.repository.ProcessedOrderRepository;
import by.raikiri.order_processing_service.repository.OutboxRepository;
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
public class OrderProcessingService {

    private final ProcessedOrderRepository repository;
    private final OutboxRepository outboxRepository;
    private final OutboxEventSerializer serializer;

    @KafkaListener(topics = "order.confirmed", groupId = "order-processing-group")
    @Transactional
    public void process(String message) throws Exception {

        OrderConfirmedEvent event = unwrap(message, OrderConfirmedEvent.class);
        UUID orderId = event.orderId();
        int iteration = event.iteration();

        audit(orderId, "PROCESSING", "STARTED");

        log.info("Processing order {}", orderId);

        if (iteration % 7 == 0) {
            repository.save(new ProcessedOrder(orderId, "failed", iteration));

            outboxRepository.save(
                    OutboxEvent.createEvent(
                            orderId,
                            "order.failed",
                            serializer.serialize(
                                    new OrderFailedEvent(
                                            orderId,
                                            "PROCESSING",
                                            "Processing failed"
                                    )
                            )
                    )
            );

            audit(orderId, "PROCESSING", "FAILED");

            log.warn("Order {} FAILED at PROCESSING", orderId);
            return;
        }

        repository.save(new ProcessedOrder(orderId, "processed", iteration));

        audit(orderId, "PROCESSING", "COMPLETED");

        log.info("Order {} PROCESSED", orderId);
    }

    @KafkaListener(topics = "order.failed", groupId = "order-processing-group")
    @Transactional
    public void rollback(String message) throws Exception {
        OrderFailedEvent event = unwrap(message, OrderFailedEvent.class);

        repository.deleteById(event.orderId());

        audit(event.orderId(), "PROCESSING", "ROLLED_BACK");

        log.info("Processing rolled back for {}", event.orderId());
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
