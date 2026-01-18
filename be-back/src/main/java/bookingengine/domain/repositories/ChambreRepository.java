package bookingengine.domain.repositories;

import bookingengine.domain.entities.Chambre;
import java.util.List;
import java.util.Optional;

public interface ChambreRepository {
    Chambre save(Chambre chambre);
    Optional<Chambre> findById(Long id);
    List<Chambre> findAll();
    void deleteById(Long id);
    List<Chambre> findByDisponible(boolean disponible);
    List<Chambre> findByType(String type);
}
