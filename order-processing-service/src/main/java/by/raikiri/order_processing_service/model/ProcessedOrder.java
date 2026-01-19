package by.raikiri.order_processing_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "processed_orders")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ProcessedOrder {
    @Id
    private UUID id;

    private String status;
    private int iteration;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProcessedOrder(UUID id, String status, int iteration) {
        this.id = id;
        this.status = status;
        this.iteration = iteration;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> objectEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != objectEffectiveClass) {
            return false;
        }
        ProcessedOrder order = (ProcessedOrder) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
