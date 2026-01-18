package bookingengine.usecase.payment;

import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.PaymentStatus;
import bookingengine.domain.events.PaymentCreatedEvent;
import bookingengine.domain.events.PaymentStatusChangedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.repositories.PaymentRepository;
import bookingengine.domain.repositories.ReservationRepository;
import bookingengine.frameworks.kafka.EventPublisher;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;

    public PaymentUseCase(PaymentRepository paymentRepository, ReservationRepository reservationRepository, EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    public Payment creerPayment(Payment payment) {
        // Validation : vérifier que la réservation existe
        reservationRepository.findById(payment.getReservationId())
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + payment.getReservationId()));

        // Définir le statut initial et la date
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment saved = paymentRepository.save(payment);
        eventPublisher.publish(PaymentCreatedEvent.of(
                saved.getId(), saved.getReservationId(), saved.getAmount(), saved.getPaymentMethod(), saved.getStatus().name()));
        return saved;
    }

    public Payment obtenirPaymentParId(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> obtenirTousLesPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> obtenirPaymentsParReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    public Payment modifierPayment(Long id, Payment payment) {
        Payment existing = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        PaymentStatus oldStatus = existing.getStatus();
        
        payment.setId(id);
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(existing.getPaymentDate());
        }

        Payment updated = paymentRepository.save(payment);
        
        // Publier un événement si le statut a changé
        if (!oldStatus.equals(updated.getStatus())) {
            eventPublisher.publish(PaymentStatusChangedEvent.of(
                    updated.getId(), oldStatus.name(), updated.getStatus().name()));
        }
        
        return updated;
    }

    public void supprimerPayment(Long id) {
        if (!paymentRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
}
