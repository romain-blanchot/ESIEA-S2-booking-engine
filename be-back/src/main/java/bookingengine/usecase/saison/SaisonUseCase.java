package bookingengine.usecase.saison;

import bookingengine.domain.entities.Saison;
import bookingengine.domain.events.SaisonCreatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.repositories.SaisonRepository;
import bookingengine.frameworks.kafka.EventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaisonUseCase {

    private final SaisonRepository saisonRepository;
    private final EventPublisher eventPublisher;

    public SaisonUseCase(SaisonRepository saisonRepository, EventPublisher eventPublisher) {
        this.saisonRepository = saisonRepository;
        this.eventPublisher = eventPublisher;
    }

    public Saison creerSaison(Saison saison) {
        Saison saved = saisonRepository.save(saison);
        eventPublisher.publish(SaisonCreatedEvent.of(
                saved.getId(), saved.getNom(), saved.getDateDebut(), saved.getDateFin(), saved.getCoefficientPrix()));
        return saved;
    }

    public Saison modifierSaison(Long id, Saison saison) {
        if (saisonRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Saison non trouvée avec l'id: " + id);
        }
        saison.setId(id);
        return saisonRepository.save(saison);
    }

    public void supprimerSaison(Long id) {
        if (saisonRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Saison non trouvée avec l'id: " + id);
        }
        saisonRepository.deleteById(id);
    }

    public Saison obtenirSaison(Long id) {
        return saisonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Saison non trouvée avec l'id: " + id));
    }

    public List<Saison> obtenirToutesSaisons() {
        return saisonRepository.findAll();
    }

    public Optional<Saison> obtenirSaisonParDate(LocalDate date) {
        return saisonRepository.findByDate(date);
    }
}
