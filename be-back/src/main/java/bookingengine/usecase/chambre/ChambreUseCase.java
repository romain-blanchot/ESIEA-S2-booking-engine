package bookingengine.usecase.chambre;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.events.ChambreCreatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.ChambreRepository;

import java.util.List;

public class ChambreUseCase {

    private final ChambreRepository chambreRepository;
    private final EventPublisherPort eventPublisher;

    public ChambreUseCase(ChambreRepository chambreRepository, EventPublisherPort eventPublisher) {
        this.chambreRepository = chambreRepository;
        this.eventPublisher = eventPublisher;
    }

    public Chambre creerChambre(Chambre chambre) {
        Chambre saved = chambreRepository.save(chambre);
        eventPublisher.publish(ChambreCreatedEvent.of(
                saved.getId(), saved.getNumero(), saved.getType(), saved.getPrixBase()));
        return saved;
    }

    public Chambre modifierChambre(Long id, Chambre chambre) {
        if (chambreRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Chambre non trouvée avec l'id: " + id);
        }
        chambre.setId(id);
        return chambreRepository.save(chambre);
    }

    public void supprimerChambre(Long id) {
        if (chambreRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Chambre non trouvée avec l'id: " + id);
        }
        chambreRepository.deleteById(id);
    }

    public Chambre obtenirChambre(Long id) {
        return chambreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chambre non trouvée avec l'id: " + id));
    }

    public List<Chambre> obtenirToutesChambres() {
        return chambreRepository.findAll();
    }

    public List<Chambre> obtenirChambresDisponibles() {
        return chambreRepository.findByDisponible(true);
    }

    public List<Chambre> obtenirChambresParType(String type) {
        return chambreRepository.findByType(type);
    }
}
