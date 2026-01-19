package by.raikiri.order_processing_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new IllegalStateException("Outbox serialization failed", e);
        }
    }
}
