package by.raikiri.order_creation_service.repository;

import by.raikiri.order_creation_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
