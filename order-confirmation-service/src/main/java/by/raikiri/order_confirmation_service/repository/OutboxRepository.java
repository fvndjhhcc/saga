package by.raikiri.order_confirmation_service.repository;

import by.raikiri.order_confirmation_service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
}
