package by.raikiri.order_creation_service.service.spec;

import by.raikiri.order_creation_service.model.SagaAuditLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SagaAuditSpecification {

    public static Specification<SagaAuditLog> filter(
            UUID orderId,
            Instant from,
            Instant to
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>(3);

            addIfNotNull(predicates, orderId,
                    id -> cb.equal(root.get("orderId"), id));

            addIfNotNull(predicates, from,
                    date -> cb.greaterThanOrEqualTo(root.get("timestamp"), date));

            addIfNotNull(predicates, to,
                    date -> cb.lessThanOrEqualTo(root.get("timestamp"), date));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static <T> void addIfNotNull(
            List<Predicate> predicates,
            T value,
            java.util.function.Function<T, Predicate> predicateFactory
    ) {
        if (value != null) {
            predicates.add(predicateFactory.apply(value));
        }
    }
}
