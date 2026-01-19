package by.raikiri.order_creation_service.controller;

import by.raikiri.order_creation_service.dto.SagaAuditResponse;
import by.raikiri.order_creation_service.model.SagaAuditLog;
import by.raikiri.order_creation_service.repository.SagaAuditRepository;
import by.raikiri.order_creation_service.service.spec.SagaAuditSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class SagaAuditController {

    private final SagaAuditRepository repository;

    @GetMapping
    public Page<SagaAuditResponse> getAudit(
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            Pageable pageable
    ) {
        return repository.findAll(
                SagaAuditSpecification.filter(orderId, from, to),
                pageable
        ).map(this::toResponse);
    }

    private SagaAuditResponse toResponse(SagaAuditLog log) {
        return SagaAuditResponse.builder()
                .orderId(log.getOrderId())
                .stage(log.getStage())
                .status(log.getStatus())
                .timestamp(log.getTimestamp())
                .build();
    }
}