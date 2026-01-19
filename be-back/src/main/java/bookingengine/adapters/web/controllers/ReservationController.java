package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.ReservationCreateRequest;
import bookingengine.adapters.web.dto.ReservationResponse;
import bookingengine.adapters.web.dto.ReservationUpdateRequest;
import bookingengine.domain.entities.Reservation;
import bookingengine.domain.entities.ReservationStatus;
import bookingengine.usecase.reservation.ReservationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("reservations")
@Tag(name = "Reservations", description = "Gestion des réservations")
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    public ReservationController(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations récupérée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> reservations = reservationUseCase.obtenirToutesLesReservations()
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("{id}")
    @Operation(summary = "Obtenir une réservation par ID")
    @ApiResponse(responseCode = "200", description = "Réservation trouvée")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationUseCase.obtenirReservationParId(id);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }

    @GetMapping("status/{status}")
    @Operation(summary = "Lister les réservations par statut")
    @ApiResponse(responseCode = "200", description = "Réservations trouvées")
    @ApiResponse(responseCode = "400", description = "Statut invalide")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<List<ReservationResponse>> getReservationsByStatus(@PathVariable String status) {
        ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        List<ReservationResponse> reservations = reservationUseCase.obtenirReservationsParStatut(reservationStatus)
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("chambre/{chambreId}")
    @Operation(summary = "Lister les réservations d'une chambre")
    @ApiResponse(responseCode = "200", description = "Réservations trouvées")
    @ApiResponse(responseCode = "404", description = "Chambre non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<List<ReservationResponse>> getReservationsByChambre(@PathVariable Long chambreId) {
        List<ReservationResponse> reservations = reservationUseCase.obtenirReservationsParChambre(chambreId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("utilisateur/{utilisateurId}")
    @Operation(summary = "Lister les réservations d'un utilisateur")
    @ApiResponse(responseCode = "200", description = "Réservations trouvées")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUtilisateur(@PathVariable Long utilisateurId) {
        List<ReservationResponse> reservations = reservationUseCase.obtenirReservationsParUtilisateur(utilisateurId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    @ApiResponse(responseCode = "201", description = "Réservation créée avec succès")
    @ApiResponse(responseCode = "404", description = "Chambre ou utilisateur non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationCreateRequest request) {
        Reservation reservation = new Reservation();
        reservation.setChambreId(request.chambreId());
        reservation.setUtilisateurId(request.utilisateurId());
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        String paymentMethod = request.paymentMethod() != null ? request.paymentMethod() : "NON_DEFINI";
        Reservation created = reservationUseCase.creerReservation(reservation, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservationResponse.from(created));
    }

    @PutMapping("{id}")
    @Operation(summary = "Modifier une réservation existante")
    @ApiResponse(responseCode = "200", description = "Réservation modifiée avec succès")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @RequestBody ReservationUpdateRequest request) {
        Reservation reservation = reservationUseCase.obtenirReservationParId(id);
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatus(ReservationStatus.valueOf(request.status()));

        Reservation updated = reservationUseCase.modifierReservation(id, reservation);
        return ResponseEntity.ok(ReservationResponse.from(updated));
    }

    @PutMapping("{id}/cancel")
    @Operation(summary = "Annuler une réservation")
    @ApiResponse(responseCode = "200", description = "Réservation annulée avec succès")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id, @RequestParam(required = false) String reason) {
        reservationUseCase.annulerReservation(id, reason != null ? reason : "Cancelled by user");
        Reservation reservation = reservationUseCase.obtenirReservationParId(id);
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Supprimer une réservation")
    @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationUseCase.supprimerReservation(id);
        return ResponseEntity.noContent().build();
    }
}
