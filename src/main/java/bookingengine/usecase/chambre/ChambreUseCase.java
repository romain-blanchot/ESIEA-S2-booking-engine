package bookingengine.usecase.chambre;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.repositories.ChambreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChambreUseCase {

    private final ChambreRepository chambreRepository;

    public ChambreUseCase(ChambreRepository chambreRepository) {
        this.chambreRepository = chambreRepository;
    }

    public Chambre creerChambre(Chambre chambre) {
        return chambreRepository.save(chambre);
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
