package bookingengine.usecase.payment;

import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.PaymentStatus;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.domain.events.PaymentCreatedEvent;
import bookingengine.domain.events.PaymentStatusChangedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.PaymentRepository;
import bookingengine.domain.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentUseCase Tests")
class PaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        paymentUseCase = new PaymentUseCase(paymentRepository, reservationRepository, eventPublisher);
    }

    @Nested
    @DisplayName("creerPayment Tests")
    class CreerPaymentTests {

        @Test
        @DisplayName("Should create payment and publish event")
        void shouldCreatePaymentAndPublishEvent() {
            Long reservationId = 1L;
            Reservation reservation = new Reservation();
            reservation.setId(reservationId);

            Payment payment = new Payment();
            payment.setReservationId(reservationId);
            payment.setAmount(BigDecimal.valueOf(300));
            payment.setPaymentMethod("VIREMENT");
            payment.setStatus(PaymentStatus.PENDING);

            Payment savedPayment = new Payment();
            savedPayment.setId(1L);
            savedPayment.setReservationId(reservationId);
            savedPayment.setAmount(BigDecimal.valueOf(300));
            savedPayment.setPaymentMethod("VIREMENT");
            savedPayment.setStatus(PaymentStatus.PENDING);
            savedPayment.setPaymentDate(LocalDateTime.now());

            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
            when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

            Payment result = paymentUseCase.creerPayment(payment);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(eventPublisher).publish(any(PaymentCreatedEvent.class));
        }

        @Test
        @DisplayName("Should throw when reservation not found")
        void shouldThrowWhenReservationNotFound() {
            Payment payment = new Payment();
            payment.setReservationId(999L);

            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> paymentUseCase.creerPayment(payment));
        }
    }

    @Nested
    @DisplayName("modifierPayment Tests")
    class ModifierPaymentTests {

        @Test
        @DisplayName("Should update payment and publish status change event")
        void shouldUpdatePaymentAndPublishStatusChangeEvent() {
            Long paymentId = 1L;
            Long reservationId = 1L;

            Payment existingPayment = new Payment();
            existingPayment.setId(paymentId);
            existingPayment.setReservationId(reservationId);
            existingPayment.setAmount(BigDecimal.valueOf(300));
            existingPayment.setStatus(PaymentStatus.PENDING);
            existingPayment.setPaymentMethod("VIREMENT");

            Payment updatedPayment = new Payment();
            updatedPayment.setId(paymentId);
            updatedPayment.setReservationId(reservationId);
            updatedPayment.setAmount(BigDecimal.valueOf(300));
            updatedPayment.setStatus(PaymentStatus.CONFIRMED);
            updatedPayment.setPaymentMethod("VIREMENT");

            Reservation reservation = new Reservation();
            reservation.setId(reservationId);
            reservation.setStatus(ReservationStatus.PENDING);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            Payment result = paymentUseCase.modifierPayment(paymentId, updatedPayment);

            assertEquals(PaymentStatus.CONFIRMED, result.getStatus());
            verify(eventPublisher).publish(any(PaymentStatusChangedEvent.class));
        }

        @Test
        @DisplayName("Should confirm reservation when payment confirmed")
        void shouldConfirmReservationWhenPaymentConfirmed() {
            Long paymentId = 1L;
            Long reservationId = 1L;

            Payment existingPayment = new Payment();
            existingPayment.setId(paymentId);
            existingPayment.setReservationId(reservationId);
            existingPayment.setStatus(PaymentStatus.PENDING);

            Payment updatedPayment = new Payment();
            updatedPayment.setId(paymentId);
            updatedPayment.setReservationId(reservationId);
            updatedPayment.setStatus(PaymentStatus.CONFIRMED);

            Reservation reservation = new Reservation();
            reservation.setId(reservationId);
            reservation.setStatus(ReservationStatus.PENDING);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            paymentUseCase.modifierPayment(paymentId, updatedPayment);

            ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(reservationCaptor.capture());
            assertEquals(ReservationStatus.CONFIRMED, reservationCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("Should cancel reservation when payment cancelled")
        void shouldCancelReservationWhenPaymentCancelled() {
            Long paymentId = 1L;
            Long reservationId = 1L;

            Payment existingPayment = new Payment();
            existingPayment.setId(paymentId);
            existingPayment.setReservationId(reservationId);
            existingPayment.setStatus(PaymentStatus.PENDING);

            Payment updatedPayment = new Payment();
            updatedPayment.setId(paymentId);
            updatedPayment.setReservationId(reservationId);
            updatedPayment.setStatus(PaymentStatus.CANCELLED);

            Reservation reservation = new Reservation();
            reservation.setId(reservationId);
            reservation.setStatus(ReservationStatus.PENDING);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            paymentUseCase.modifierPayment(paymentId, updatedPayment);

            ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(reservationCaptor.capture());
            assertEquals(ReservationStatus.CANCELLED, reservationCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("Should cancel reservation when payment refunded")
        void shouldCancelReservationWhenPaymentRefunded() {
            Long paymentId = 1L;
            Long reservationId = 1L;

            Payment existingPayment = new Payment();
            existingPayment.setId(paymentId);
            existingPayment.setReservationId(reservationId);
            existingPayment.setStatus(PaymentStatus.CONFIRMED);

            Payment updatedPayment = new Payment();
            updatedPayment.setId(paymentId);
            updatedPayment.setReservationId(reservationId);
            updatedPayment.setStatus(PaymentStatus.REFUNDED);

            Reservation reservation = new Reservation();
            reservation.setId(reservationId);
            reservation.setStatus(ReservationStatus.CONFIRMED);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            paymentUseCase.modifierPayment(paymentId, updatedPayment);

            ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(reservationRepository).save(reservationCaptor.capture());
            assertEquals(ReservationStatus.CANCELLED, reservationCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("Should not publish event when status unchanged")
        void shouldNotPublishEventWhenStatusUnchanged() {
            Long paymentId = 1L;

            Payment existingPayment = new Payment();
            existingPayment.setId(paymentId);
            existingPayment.setReservationId(1L);
            existingPayment.setStatus(PaymentStatus.PENDING);

            Payment updatedPayment = new Payment();
            updatedPayment.setId(paymentId);
            updatedPayment.setReservationId(1L);
            updatedPayment.setStatus(PaymentStatus.PENDING);

            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

            paymentUseCase.modifierPayment(paymentId, updatedPayment);

            verify(eventPublisher, never()).publish(any(PaymentStatusChangedEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when payment not found")
        void shouldThrowExceptionWhenPaymentNotFound() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            Payment payment = new Payment();
            payment.setStatus(PaymentStatus.CONFIRMED);

            assertThrows(EntityNotFoundException.class,
                    () -> paymentUseCase.modifierPayment(999L, payment));
        }
    }

    @Nested
    @DisplayName("obtenirPayments Tests")
    class ObtenirPaymentsTests {

        @Test
        @DisplayName("Should get payment by id")
        void shouldGetPaymentById() {
            Payment payment = new Payment();
            payment.setId(1L);

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            Payment result = paymentUseCase.obtenirPaymentParId(1L);

            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw when payment not found")
        void shouldThrowWhenPaymentNotFound() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> paymentUseCase.obtenirPaymentParId(999L));
        }

        @Test
        @DisplayName("Should get all payments")
        void shouldGetAllPayments() {
            Payment p1 = new Payment();
            p1.setId(1L);
            Payment p2 = new Payment();
            p2.setId(2L);

            when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));

            List<Payment> result = paymentUseCase.obtenirTousLesPayments();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should get payments by reservation")
        void shouldGetPaymentsByReservation() {
            Payment payment = new Payment();
            payment.setId(1L);
            payment.setReservationId(5L);

            when(paymentRepository.findByReservationId(5L)).thenReturn(List.of(payment));

            List<Payment> result = paymentUseCase.obtenirPaymentsParReservation(5L);

            assertEquals(1, result.size());
            assertEquals(5L, result.get(0).getReservationId());
        }
    }

    @Nested
    @DisplayName("supprimerPayment Tests")
    class SupprimerPaymentTests {

        @Test
        @DisplayName("Should delete payment")
        void shouldDeletePayment() {
            Payment payment = new Payment();
            payment.setId(1L);
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            paymentUseCase.supprimerPayment(1L);

            verify(paymentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw when payment not found for deletion")
        void shouldThrowWhenPaymentNotFoundForDeletion() {
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> paymentUseCase.supprimerPayment(999L));
        }
    }
}
