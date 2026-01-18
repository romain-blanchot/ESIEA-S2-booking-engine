package bookingengine.frameworks.config;

import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.PaymentRepository;
import bookingengine.domain.repositories.ReservationRepository;
import bookingengine.domain.repositories.SaisonRepository;
import bookingengine.frameworks.kafka.EventPublisher;
import bookingengine.usecase.chambre.ChambreUseCase;
import bookingengine.usecase.payment.PaymentUseCase;
import bookingengine.usecase.prix.CalculPrixUseCase;
import bookingengine.usecase.reservation.ReservationUseCase;
import bookingengine.usecase.saison.SaisonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public PaymentUseCase paymentUseCase(PaymentRepository paymentRepository, 
                                         ReservationRepository reservationRepository,
                                         EventPublisher eventPublisher) {
        return new PaymentUseCase(paymentRepository, reservationRepository, eventPublisher);
    }

    @Bean
    public ReservationUseCase reservationUseCase(ReservationRepository reservationRepository,
                                                 EventPublisher eventPublisher) {
        return new ReservationUseCase(reservationRepository, eventPublisher);
    }

    @Bean
    public ChambreUseCase chambreUseCase(ChambreRepository chambreRepository,
                                         EventPublisher eventPublisher) {
        return new ChambreUseCase(chambreRepository, eventPublisher);
    }

    @Bean
    public SaisonUseCase saisonUseCase(SaisonRepository saisonRepository,
                                       EventPublisher eventPublisher) {
        return new SaisonUseCase(saisonRepository, eventPublisher);
    }

    @Bean
    public CalculPrixUseCase calculPrixUseCase(ChambreRepository chambreRepository,
                                               SaisonRepository saisonRepository,
                                               EventPublisher eventPublisher) {
        return new CalculPrixUseCase(chambreRepository, saisonRepository, eventPublisher);
    }
}
