package by.raikiri.order_confirmation_service.repository;

import by.raikiri.order_confirmation_service.model.ConfirmedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfirmedOrderRepository extends JpaRepository<ConfirmedOrder, UUID> {
}