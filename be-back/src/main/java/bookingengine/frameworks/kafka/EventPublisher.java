package bookingengine.frameworks.kafka;

import bookingengine.domain.events.ChambreCreatedEvent;
import bookingengine.domain.events.PrixCalculatedEvent;
import bookingengine.domain.events.SaisonCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void publish(ChambreCreatedEvent event) {
        sendEvent(KafkaConfig.TOPIC_CHAMBRES, event.chambreId().toString(), event);
    }

    public void publish(SaisonCreatedEvent event) {
        sendEvent(KafkaConfig.TOPIC_SAISONS, event.saisonId().toString(), event);
    }

    public void publish(PrixCalculatedEvent event) {
        sendEvent(KafkaConfig.TOPIC_PRIX, event.chambreId().toString(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Erreur envoi event Kafka: {}", ex.getMessage());
                        } else {
                            log.info("Event publié sur {} : {}", topic, json);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Erreur sérialisation event: {}", e.getMessage());
        }
    }
}
