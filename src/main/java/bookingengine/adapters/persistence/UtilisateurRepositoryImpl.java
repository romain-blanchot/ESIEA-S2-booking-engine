package bookingengine.adapters.persistence;

import bookingengine.adapters.persistence.entities.UtilisateurJpaEntity;
import bookingengine.adapters.persistence.mappers.UtilisateurMapper;
import bookingengine.adapters.persistence.repositories.UtilisateurJpaRepository;
import bookingengine.domain.entities.Utilisateur;
import bookingengine.domain.repositories.UtilisateurRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    private final UtilisateurJpaRepository jpaRepository;
    private final UtilisateurMapper mapper;

    public UtilisateurRepositoryImpl(UtilisateurJpaRepository jpaRepository, UtilisateurMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        UtilisateurJpaEntity entity = mapper.toEntity(utilisateur);
        UtilisateurJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Utilisateur> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
