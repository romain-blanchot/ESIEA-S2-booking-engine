package bookingengine.domain.repositories;

import bookingengine.domain.entities.Utilisateur;
import java.util.Optional;

public interface UtilisateurRepository {
    Utilisateur save(Utilisateur utilisateur);
    Optional<Utilisateur> findById(Long id);
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
