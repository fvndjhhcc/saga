package by.raikiri.order_creation_service.service;

import by.raikiri.order_creation_service.dto.SagaAuditEvent;
import by.raikiri.order_creation_service.model.SagaAuditLog;
import by.raikiri.order_creation_service.repository.SagaAuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaAuditConsumer {

    private final SagaAuditRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order.saga.audit",
            groupId = "order-saga-audit-group",
            containerFactory = "kafkaBatchListenerContainerFactory"
    )
    @Transactional
    public void consume(List<String> messages) throws Exception {

        List<SagaAuditLog> logs = messages.stream()
                .map(msg -> unwrap(msg))
                .map(evt -> SagaAuditLog.builder()
                        .orderId(evt.orderId())
                        .stage(evt.stage())
                        .status(evt.status())
                        .timestamp(evt.timestamp())
                        .build()
                )
                .toList();

        repository.saveAll(logs);

        log.debug("Saved {} saga audit records", logs.size());
    }

    private SagaAuditEvent unwrap(String message) {
        try {
            return objectMapper.readValue(
                    objectMapper.readValue(message, String.class),
                    SagaAuditEvent.class
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid audit message " + message, e);
        }
    }
}
