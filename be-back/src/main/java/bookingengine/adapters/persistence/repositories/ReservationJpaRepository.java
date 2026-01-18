package bookingengine.adapters.persistence.repositories;

import bookingengine.adapters.persistence.entities.ReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {
    List<ReservationJpaEntity> findByStatus(ReservationJpaEntity.ReservationStatusJpa status);
    List<ReservationJpaEntity> findByChambreId(Long chambreId);
    List<ReservationJpaEntity> findByUtilisateurId(Long utilisateurId);
}
