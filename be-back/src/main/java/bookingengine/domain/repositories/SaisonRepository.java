package bookingengine.domain.repositories;

import bookingengine.domain.entities.Saison;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaisonRepository {
    Saison save(Saison saison);
    Optional<Saison> findById(Long id);
    List<Saison> findAll();
    void deleteById(Long id);
    Optional<Saison> findByDate(LocalDate date);
}
