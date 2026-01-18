package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.ReservationJpaEntity;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toDomain(ReservationJpaEntity entity) {
        if (entity == null) return null;
        return new Reservation(
                entity.getId(),
                entity.getChambreId(),
                entity.getUtilisateurId(),
                entity.getDateDebut(),
                entity.getDateFin(),
                ReservationStatus.valueOf(entity.getStatus().name()),
                entity.getCreatedAt(),
                entity.getCancelledAt()
        );
    }

    public ReservationJpaEntity toEntity(Reservation domain) {
        if (domain == null) return null;
        ReservationJpaEntity entity = new ReservationJpaEntity();
        if (domain.getId() != null && domain.getId() > 0) {
            entity.setId(domain.getId());
        }
        entity.setChambreId(domain.getChambreId());
        entity.setUtilisateurId(domain.getUtilisateurId());
        entity.setDateDebut(domain.getDateDebut());
        entity.setDateFin(domain.getDateFin());
        entity.setStatus(ReservationJpaEntity.ReservationStatusJpa.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCancelledAt(domain.getCancelledAt());
        return entity;
    }
}
