package bookingengine.frameworks.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_CHAMBRES = "booking.chambres";
    public static final String TOPIC_SAISONS = "booking.saisons";
    public static final String TOPIC_PRIX = "booking.prix";
    public static final String TOPIC_PAYMENTS = "booking.payments";
    public static final String TOPIC_RESERVATIONS = "booking.reservations";

    @Bean
    public NewTopic chambresTopic() {
        return TopicBuilder.name(TOPIC_CHAMBRES)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic saisonsTopic() {
        return TopicBuilder.name(TOPIC_SAISONS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic prixTopic() {
        return TopicBuilder.name(TOPIC_PRIX)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name(TOPIC_PAYMENTS)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationsTopic() {
        return TopicBuilder.name(TOPIC_RESERVATIONS)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
