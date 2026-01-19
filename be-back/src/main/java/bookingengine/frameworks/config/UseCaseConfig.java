package bookingengine.frameworks.config;

import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.ports.PasswordEncoderPort;
import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.SaisonRepository;
import bookingengine.domain.repositories.UtilisateurRepository;
import bookingengine.usecase.auth.AuthUseCase;
import bookingengine.usecase.chambre.ChambreUseCase;
import bookingengine.usecase.prix.CalculPrixUseCase;
import bookingengine.usecase.saison.SaisonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public SaisonUseCase saisonUseCase(SaisonRepository saisonRepository, EventPublisherPort eventPublisher) {
        return new SaisonUseCase(saisonRepository, eventPublisher);
    }

    @Bean
    public ChambreUseCase chambreUseCase(ChambreRepository chambreRepository, EventPublisherPort eventPublisher) {
        return new ChambreUseCase(chambreRepository, eventPublisher);
    }

    @Bean
    public CalculPrixUseCase calculPrixUseCase(ChambreRepository chambreRepository,
                                                SaisonRepository saisonRepository,
                                                EventPublisherPort eventPublisher) {
        return new CalculPrixUseCase(chambreRepository, saisonRepository, eventPublisher);
    }

    @Bean
    public AuthUseCase authUseCase(UtilisateurRepository utilisateurRepository, PasswordEncoderPort passwordEncoder) {
        return new AuthUseCase(utilisateurRepository, passwordEncoder);
    }
}
