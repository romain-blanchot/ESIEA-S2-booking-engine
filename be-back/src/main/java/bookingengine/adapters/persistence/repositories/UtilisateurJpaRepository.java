package bookingengine.adapters.persistence.repositories;

import bookingengine.adapters.persistence.entities.UtilisateurJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurJpaRepository extends JpaRepository<UtilisateurJpaEntity, Long> {
    Optional<UtilisateurJpaEntity> findByUsername(String username);
    Optional<UtilisateurJpaEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
