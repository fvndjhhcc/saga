package by.raikiri.order_creation_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_audit_log",
       indexes = {
           @Index(name = "idx_saga_audit_order", columnList = "orderId"),
           @Index(name = "idx_saga_audit_timestamp", columnList = "timestamp")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID orderId;

    private String stage;

    private String status;

    private Instant timestamp;
}
