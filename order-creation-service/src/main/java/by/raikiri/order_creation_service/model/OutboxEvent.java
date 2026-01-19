package by.raikiri.order_creation_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    @Id
    private UUID id;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "event_type")
    private String eventType;

    @ColumnTransformer(write = "?::jsonb")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public static OutboxEvent createEvent(UUID aggregateId, String eventType, String payload) {
        return new OutboxEvent(UUID.randomUUID(), aggregateId, eventType, payload, LocalDateTime.now());
    }
}
