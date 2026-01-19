package bookingengine.usecase.reservation;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.PaymentStatus;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.domain.events.ReservationCreatedEvent;
import bookingengine.domain.events.ReservationCancelledEvent;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.domain.ports.EventPublisherPort;
import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.PaymentRepository;
import bookingengine.domain.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final PaymentRepository paymentRepository;
    private final EventPublisherPort eventPublisher;

    public ReservationUseCase(ReservationRepository reservationRepository,
                              ChambreRepository chambreRepository,
                              PaymentRepository paymentRepository,
                              EventPublisherPort eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.chambreRepository = chambreRepository;
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    public Reservation creerReservation(Reservation reservation) {
        return creerReservation(reservation, "NON_DEFINI");
    }

    public Reservation creerReservation(Reservation reservation, String paymentMethod) {
        // Validation des dates
        if (reservation.getDateDebut().isAfter(reservation.getDateFin())) {
            throw new IllegalArgumentException("La date de debut doit etre avant la date de fin");
        }

        // Vérifier que la chambre existe et est à la vente
        Chambre chambre = chambreRepository.findById(reservation.getChambreId())
                .orElseThrow(() -> new EntityNotFoundException("Chambre not found with id: " + reservation.getChambreId()));

        if (!chambre.isDisponible()) {
            throw new IllegalStateException("Cette chambre n'est pas disponible a la reservation (hors service)");
        }

        // Vérifier qu'il n'y a pas de conflits de dates avec d'autres réservations
        List<Reservation> conflits = reservationRepository.findConflictingReservations(
                reservation.getChambreId(), reservation.getDateDebut(), reservation.getDateFin());
        if (!conflits.isEmpty()) {
            throw new IllegalStateException("La chambre est deja reservee pour les dates selectionnees");
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

        // Créer automatiquement un paiement en attente pour la réservation
        createPaymentForReservation(saved, chambre, paymentMethod);

        return saved;
    }

    private void createPaymentForReservation(Reservation reservation, Chambre chambre, String paymentMethod) {
        // Calculer le nombre de nuits
        long nombreNuits = ChronoUnit.DAYS.between(reservation.getDateDebut(), reservation.getDateFin());
        if (nombreNuits <= 0) {
            nombreNuits = 1; // Minimum une nuit
        }

        // Calculer le montant total (prix de base * nombre de nuits)
        BigDecimal montantTotal = BigDecimal.valueOf(chambre.getPrixBase())
                .multiply(BigDecimal.valueOf(nombreNuits));

        // Créer le paiement
        Payment payment = new Payment();
        payment.setReservationId(reservation.getId());
        payment.setAmount(montantTotal);
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "NON_DEFINI");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);
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
            throw new IllegalArgumentException("La date de debut doit etre avant la date de fin");
        }

        // Vérifier la disponibilité si les dates ou la chambre changent
        boolean datesChanged = !reservation.getDateDebut().equals(existing.getDateDebut())
                || !reservation.getDateFin().equals(existing.getDateFin());
        boolean chambreChanged = !reservation.getChambreId().equals(existing.getChambreId());

        if (datesChanged || chambreChanged) {
            List<Reservation> conflits = reservationRepository.findConflictingReservations(
                    reservation.getChambreId(), reservation.getDateDebut(), reservation.getDateFin())
                    .stream()
                    .filter(r -> !r.getId().equals(id)) // Exclure la réservation actuelle
                    .toList();
            if (!conflits.isEmpty()) {
                throw new IllegalStateException("La chambre n'est pas disponible pour les dates selectionnees");
            }
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

    public boolean verifierDisponibilite(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        List<Reservation> conflits = reservationRepository.findConflictingReservations(chambreId, dateDebut, dateFin);
        return conflits.isEmpty();
    }

    public List<Reservation> obtenirReservationsConflictuelles(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        return reservationRepository.findConflictingReservations(chambreId, dateDebut, dateFin);
    }
}
