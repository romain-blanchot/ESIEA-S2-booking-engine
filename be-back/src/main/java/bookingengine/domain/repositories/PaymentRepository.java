package bookingengine.domain.repositories;

import bookingengine.domain.entities.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    void deleteById(Long id);
    List<Payment> findByReservationId(Long reservationId);
}
