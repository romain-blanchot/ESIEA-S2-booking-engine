package bookingengine.usecase.reservation;

import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.domain.events.ReservationCreatedEvent;
import bookingengine.domain.events.ReservationCancelledEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.repositories.ReservationRepository;
import bookingengine.frameworks.kafka.EventPublisher;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;

    public ReservationUseCase(ReservationRepository reservationRepository, EventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    public Reservation creerReservation(Reservation reservation) {
        // Validation des dates
        if (reservation.getDateDebut().isAfter(reservation.getDateFin())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        // Définir les valeurs par défaut
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }
        if (reservation.getCreatedAt() == null) {
            reservation.setCreatedAt(LocalDateTime.now());
        }

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.publish(ReservationCreatedEvent.of(
                saved.getId(), saved.getChambreId(), saved.getUtilisateurId(), 
                saved.getDateDebut(), saved.getDateFin(), saved.getStatus().name()));
        return saved;
    }

    public Reservation obtenirReservationParId(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
    }

    public List<Reservation> obtenirToutesLesReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> obtenirReservationsParStatut(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> obtenirReservationsParChambre(Long chambreId) {
        return reservationRepository.findByChambreId(chambreId);
    }

    public List<Reservation> obtenirReservationsParUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    public Reservation modifierReservation(Long id, Reservation reservation) {
        Reservation existing = reservationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        // Validation des dates
        if (reservation.getDateDebut().isAfter(reservation.getDateFin())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        reservation.setId(id);
        reservation.setCreatedAt(existing.getCreatedAt());

        // Gérer la date d'annulation
        if (reservation.getStatus() == ReservationStatus.CANCELLED
                && existing.getStatus() != ReservationStatus.CANCELLED) {
            reservation.setCancelledAt(LocalDateTime.now());
        }

        return reservationRepository.save(reservation);
    }

    public void supprimerReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found with id: " + id);
        }
        
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
        
        // Publier un événement d'annulation avant suppression
        if (reservation.getStatus() != ReservationStatus.CANCELLED) {
            eventPublisher.publish(ReservationCancelledEvent.of(id, "Deletion"));
        }
        
        reservationRepository.deleteById(id);
    }

    public void annulerReservation(Long id, String reason) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        
        reservationRepository.save(reservation);
        eventPublisher.publish(ReservationCancelledEvent.of(id, reason));
    }
}
