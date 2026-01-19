package bookingengine.frameworks.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "booking-engine");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Ne pas utiliser de MessageConverter - recevoir les String directement
        return factory;
    }

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
