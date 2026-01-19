package bookingengine.usecase.reservation;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.domain.events.ReservationCancelledEvent;
import bookingengine.domain.events.ReservationCreatedEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.ChambreRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationUseCase Tests")
class ReservationUseCaseTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    private ReservationUseCase reservationUseCase;

    @BeforeEach
    void setUp() {
        reservationUseCase = new ReservationUseCase(
                reservationRepository, chambreRepository, paymentRepository, eventPublisher);
    }

    @Nested
    @DisplayName("creerReservation Tests")
    class CreerReservationTests {

        @Test
        @DisplayName("Should create reservation successfully")
        void shouldCreateReservationSuccessfully() {
            Long chambreId = 1L;
            Long utilisateurId = 1L;
            LocalDate debut = LocalDate.of(2026, 3, 1);
            LocalDate fin = LocalDate.of(2026, 3, 5);

            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);
            Reservation reservation = new Reservation();
            reservation.setChambreId(chambreId);
            reservation.setUtilisateurId(utilisateurId);
            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);

            Reservation savedReservation = new Reservation();
            savedReservation.setId(1L);
            savedReservation.setChambreId(chambreId);
            savedReservation.setUtilisateurId(utilisateurId);
            savedReservation.setDateDebut(debut);
            savedReservation.setDateFin(fin);
            savedReservation.setStatus(ReservationStatus.PENDING);
            savedReservation.setCreatedAt(LocalDateTime.now());

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(reservationRepository.findConflictingReservations(eq(chambreId), eq(debut), eq(fin)))
                    .thenReturn(Collections.emptyList());
            when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

            Reservation result = reservationUseCase.creerReservation(reservation, "ESPECES");

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(ReservationStatus.PENDING, result.getStatus());
            verify(eventPublisher).publish(any(ReservationCreatedEvent.class));
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("Should throw exception when chambre not found")
        void shouldThrowExceptionWhenChambreNotFound() {
            Long chambreId = 999L;
            Reservation reservation = new Reservation();
            reservation.setChambreId(chambreId);
            reservation.setDateDebut(LocalDate.of(2026, 3, 1));
            reservation.setDateFin(LocalDate.of(2026, 3, 5));

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> reservationUseCase.creerReservation(reservation, "ESPECES"));
        }

        @Test
        @DisplayName("Should throw exception when chambre not disponible")
        void shouldThrowExceptionWhenChambreNotDisponible() {
            Long chambreId = 1L;
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", false);

            Reservation reservation = new Reservation();
            reservation.setChambreId(chambreId);
            reservation.setDateDebut(LocalDate.of(2026, 3, 1));
            reservation.setDateFin(LocalDate.of(2026, 3, 5));

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> reservationUseCase.creerReservation(reservation, "ESPECES"));

            assertTrue(exception.getMessage().contains("hors service"));
        }

        @Test
        @DisplayName("Should throw exception when dates conflict")
        void shouldThrowExceptionWhenDatesConflict() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2026, 3, 1);
            LocalDate fin = LocalDate.of(2026, 3, 5);

            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);
            Reservation existingReservation = new Reservation();
            existingReservation.setId(99L);

            Reservation reservation = new Reservation();
            reservation.setChambreId(chambreId);
            reservation.setDateDebut(debut);
            reservation.setDateFin(fin);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(reservationRepository.findConflictingReservations(eq(chambreId), eq(debut), eq(fin)))
                    .thenReturn(List.of(existingReservation));

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> reservationUseCase.creerReservation(reservation, "ESPECES"));

            assertTrue(exception.getMessage().contains("deja reservee"));
        }

        @Test
        @DisplayName("Should throw exception when end date before start date")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            Reservation reservation = new Reservation();
            reservation.setChambreId(1L);
            reservation.setDateDebut(LocalDate.of(2026, 3, 10));
            reservation.setDateFin(LocalDate.of(2026, 3, 5));

            assertThrows(IllegalArgumentException.class,
                    () -> reservationUseCase.creerReservation(reservation, "ESPECES"));
        }

        @Test
        @DisplayName("Should create payment automatically")
        void shouldCreatePaymentAutomatically() {
            Long chambreId = 1L;
            Chambre chambre = new Chambre(chambreId, "101", "Double", 100.0, 2, "Desc", true);

            Reservation reservation = new Reservation();
            reservation.setChambreId(chambreId);
            reservation.setUtilisateurId(1L);
            reservation.setDateDebut(LocalDate.of(2026, 3, 1));
            reservation.setDateFin(LocalDate.of(2026, 3, 4)); // 3 nights

            Reservation savedReservation = new Reservation();
            savedReservation.setId(1L);
            savedReservation.setChambreId(chambreId);
            savedReservation.setUtilisateurId(1L);
            savedReservation.setDateDebut(LocalDate.of(2026, 3, 1));
            savedReservation.setDateFin(LocalDate.of(2026, 3, 4));
            savedReservation.setStatus(ReservationStatus.PENDING);

            when(chambreRepository.findById(chambreId)).thenReturn(Optional.of(chambre));
            when(reservationRepository.findConflictingReservations(any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(reservationRepository.save(any())).thenReturn(savedReservation);

            reservationUseCase.creerReservation(reservation, "VIREMENT");

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository).save(paymentCaptor.capture());

            Payment createdPayment = paymentCaptor.getValue();
            assertEquals(1L, createdPayment.getReservationId());
            assertEquals("VIREMENT", createdPayment.getPaymentMethod());
            // 3 nights * 100 = 300
            assertEquals(300.0, createdPayment.getAmount().doubleValue(), 0.01);
        }
    }

    @Nested
    @DisplayName("annulerReservation Tests")
    class AnnulerReservationTests {

        @Test
        @DisplayName("Should cancel reservation and publish event")
        void shouldCancelReservationAndPublishEvent() {
            Long reservationId = 1L;
            Reservation reservation = new Reservation();
            reservation.setId(reservationId);
            reservation.setStatus(ReservationStatus.PENDING);

            when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

            reservationUseCase.annulerReservation(reservationId, "Client request");

            assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
            assertNotNull(reservation.getCancelledAt());
            verify(reservationRepository).save(reservation);
            verify(eventPublisher).publish(any(ReservationCancelledEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when reservation not found")
        void shouldThrowExceptionWhenReservationNotFound() {
            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> reservationUseCase.annulerReservation(999L, "Test"));
        }
    }

    @Nested
    @DisplayName("verifierDisponibilite Tests")
    class VerifierDisponibiliteTests {

        @Test
        @DisplayName("Should return true when no conflicts")
        void shouldReturnTrueWhenNoConflicts() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2026, 3, 1);
            LocalDate fin = LocalDate.of(2026, 3, 5);

            when(reservationRepository.findConflictingReservations(chambreId, debut, fin))
                    .thenReturn(Collections.emptyList());

            boolean result = reservationUseCase.verifierDisponibilite(chambreId, debut, fin);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when conflicts exist")
        void shouldReturnFalseWhenConflictsExist() {
            Long chambreId = 1L;
            LocalDate debut = LocalDate.of(2026, 3, 1);
            LocalDate fin = LocalDate.of(2026, 3, 5);

            Reservation conflict = new Reservation();
            conflict.setId(99L);

            when(reservationRepository.findConflictingReservations(chambreId, debut, fin))
                    .thenReturn(List.of(conflict));

            boolean result = reservationUseCase.verifierDisponibilite(chambreId, debut, fin);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should throw exception for invalid date range")
        void shouldThrowExceptionForInvalidDateRange() {
            assertThrows(IllegalArgumentException.class,
                    () -> reservationUseCase.verifierDisponibilite(1L,
                            LocalDate.of(2026, 3, 10),
                            LocalDate.of(2026, 3, 5)));
        }
    }

    @Nested
    @DisplayName("obtenirReservations Tests")
    class ObtenirReservationsTests {

        @Test
        @DisplayName("Should get reservation by id")
        void shouldGetReservationById() {
            Reservation reservation = new Reservation();
            reservation.setId(1L);

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            Reservation result = reservationUseCase.obtenirReservationParId(1L);

            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should get reservations by utilisateur")
        void shouldGetReservationsByUtilisateur() {
            Reservation r1 = new Reservation();
            r1.setId(1L);
            Reservation r2 = new Reservation();
            r2.setId(2L);

            when(reservationRepository.findByUtilisateurId(1L)).thenReturn(List.of(r1, r2));

            List<Reservation> result = reservationUseCase.obtenirReservationsParUtilisateur(1L);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should get reservations by status")
        void shouldGetReservationsByStatus() {
            Reservation r1 = new Reservation();
            r1.setStatus(ReservationStatus.PENDING);

            when(reservationRepository.findByStatus(ReservationStatus.PENDING)).thenReturn(List.of(r1));

            List<Reservation> result = reservationUseCase.obtenirReservationsParStatut(ReservationStatus.PENDING);

            assertEquals(1, result.size());
            assertEquals(ReservationStatus.PENDING, result.get(0).getStatus());
        }
    }
}
