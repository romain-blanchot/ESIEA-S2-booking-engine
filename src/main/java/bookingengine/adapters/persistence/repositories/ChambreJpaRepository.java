package bookingengine.adapters.persistence.repositories;

import bookingengine.adapters.persistence.entities.ChambreJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChambreJpaRepository extends JpaRepository<ChambreJpaEntity, Long> {
    List<ChambreJpaEntity> findByDisponible(boolean disponible);
    List<ChambreJpaEntity> findByType(String type);
}
