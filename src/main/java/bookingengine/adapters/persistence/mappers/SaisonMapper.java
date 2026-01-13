package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.SaisonJpaEntity;
import bookingengine.domain.entities.Saison;
import org.springframework.stereotype.Component;

@Component
public class SaisonMapper {

    public Saison toDomain(SaisonJpaEntity entity) {
        if (entity == null) return null;
        return new Saison(
                entity.getId(),
                entity.getNom(),
                entity.getDateDebut(),
                entity.getDateFin(),
                entity.getCoefficientPrix()
        );
    }

    public SaisonJpaEntity toEntity(Saison domain) {
        if (domain == null) return null;
        SaisonJpaEntity entity = new SaisonJpaEntity();
        if (domain.getId() != null && domain.getId() > 0) {
            entity.setId(domain.getId());
        }
        entity.setNom(domain.getNom());
        entity.setDateDebut(domain.getDateDebut());
        entity.setDateFin(domain.getDateFin());
        entity.setCoefficientPrix(domain.getCoefficientPrix());
        return entity;
    }
}
