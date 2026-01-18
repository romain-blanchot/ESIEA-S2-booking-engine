package bookingengine.frameworks.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    @KafkaListener(topics = KafkaConfig.TOPIC_CHAMBRES, groupId = "booking-engine")
    public void handleChambreEvent(String message) {
        log.info("Reçu event chambre: {}", message);
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_SAISONS, groupId = "booking-engine")
    public void handleSaisonEvent(String message) {
        log.info("Reçu event saison: {}", message);
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_PRIX, groupId = "booking-engine")
    public void handlePrixEvent(String message) {
        log.info("Reçu event prix: {}", message);
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_PAYMENTS, groupId = "booking-engine")
    public void handlePaymentEvent(String message) {
        log.info("Reçu event payment: {}", message);
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_RESERVATIONS, groupId = "booking-engine")
    public void handleReservationEvent(String message) {
        log.info("Reçu event reservation: {}", message);
    }
}
