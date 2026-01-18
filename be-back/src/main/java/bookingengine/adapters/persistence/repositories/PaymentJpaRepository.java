package bookingengine.adapters.persistence.repositories;

import bookingengine.adapters.persistence.entities.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    List<PaymentJpaEntity> findByReservationId(Long reservationId);
}
