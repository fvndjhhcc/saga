package by.raikiri.order_creation_service.repository;

import by.raikiri.order_creation_service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
}
