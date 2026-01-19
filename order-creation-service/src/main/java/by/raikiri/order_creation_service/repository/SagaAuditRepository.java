package by.raikiri.order_creation_service.repository;

import by.raikiri.order_creation_service.model.SagaAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SagaAuditRepository extends JpaRepository<SagaAuditLog, Long>, JpaSpecificationExecutor<SagaAuditLog> {
}
