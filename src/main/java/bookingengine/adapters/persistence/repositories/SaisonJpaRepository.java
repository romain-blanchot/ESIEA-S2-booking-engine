package bookingengine.adapters.persistence.repositories;

import bookingengine.adapters.persistence.entities.SaisonJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SaisonJpaRepository extends JpaRepository<SaisonJpaEntity, Long> {

    @Query("SELECT s FROM SaisonJpaEntity s WHERE :date BETWEEN s.dateDebut AND s.dateFin")
    Optional<SaisonJpaEntity> findByDate(@Param("date") LocalDate date);
}
