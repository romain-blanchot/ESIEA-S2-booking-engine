package bookingengine.domain.repositories;

import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findAll();
    void deleteById(Long id);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByChambreId(Long chambreId);
    List<Reservation> findByUtilisateurId(Long utilisateurId);
    boolean existsById(Long id);
}
