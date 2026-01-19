package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.ChambreJpaEntity;
import bookingengine.domain.entities.Chambre;
import org.springframework.stereotype.Component;

@Component
public class ChambreMapper {

    public Chambre toDomain(ChambreJpaEntity entity) {
        if (entity == null) return null;
        return new Chambre(
                entity.getId(),
                entity.getNumero(),
                entity.getType(),
                entity.getPrixBase(),
                entity.getCapacite(),
                entity.getDescription(),
                entity.isDisponible()
        );
    }

    public ChambreJpaEntity toEntity(Chambre domain) {
        if (domain == null) return null;
        ChambreJpaEntity entity = new ChambreJpaEntity();
        if (domain.getId() != null && domain.getId() > 0) {
            entity.setId(domain.getId());
        }
        entity.setNumero(domain.getNumero());
        entity.setType(domain.getType());
        entity.setPrixBase(domain.getPrixBase());
        entity.setCapacite(domain.getCapacite());
        entity.setDescription(domain.getDescription());
        entity.setDisponible(domain.isDisponible());
        return entity;
    }
}
