package bookingengine.adapters.persistence.mappers;

import bookingengine.adapters.persistence.entities.PaymentJpaEntity;
import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toDomain(PaymentJpaEntity entity) {
        if (entity == null) return null;
        return new Payment(
                entity.getId(),
                entity.getReservationId(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                PaymentStatus.valueOf(entity.getStatus().name()),
                entity.getPaymentDate()
        );
    }

    public PaymentJpaEntity toEntity(Payment domain) {
        if (domain == null) return null;
        PaymentJpaEntity entity = new PaymentJpaEntity();
        if (domain.getId() != null && domain.getId() > 0) {
            entity.setId(domain.getId());
        }
        entity.setReservationId(domain.getReservationId());
        entity.setAmount(domain.getAmount());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setStatus(PaymentJpaEntity.PaymentStatusJpa.valueOf(domain.getStatus().name()));
        entity.setPaymentDate(domain.getPaymentDate());
        return entity;
    }
}
