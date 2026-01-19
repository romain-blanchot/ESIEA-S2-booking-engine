package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.UtilisateurJpaEntity;
import bookingengine.domain.entities.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public Utilisateur toDomain(UtilisateurJpaEntity entity) {
        if (entity == null) return null;
        return new Utilisateur(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getRole()
        );
    }

    public UtilisateurJpaEntity toEntity(Utilisateur domain) {
        if (domain == null) return null;
        UtilisateurJpaEntity entity = new UtilisateurJpaEntity();
        if (domain.getId() != null && domain.getId() > 0) {
            entity.setId(domain.getId());
        }
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole());
        return entity;
    }
}
