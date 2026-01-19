package by.raikiri.order_processing_service.repository;

import by.raikiri.order_processing_service.model.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, UUID> {
}
