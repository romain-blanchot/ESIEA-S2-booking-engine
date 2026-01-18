package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.PaymentCreateRequest;
import bookingengine.adapters.web.dto.PaymentResponse;
import bookingengine.adapters.web.dto.PaymentUpdateRequest;
import bookingengine.domain.entities.Payment;
import bookingengine.domain.entities.PaymentStatus;
import bookingengine.usecase.payment.PaymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("payments")
@Tag(name = "Payments", description = "Gestion des paiements")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @GetMapping
    @Operation(summary = "Lister tous les paiements")
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération des paiements")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentUseCase.obtenirTousLesPayments()
                .stream()
                .map(PaymentResponse::from)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("{id}")
    @Operation(summary = "Obtenir un paiement par ID")
    @ApiResponse(responseCode = "200", description = "Paiement trouvé et retourné")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé avec l'ID fourni")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération du paiement")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentUseCase.obtenirPaymentParId(id);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @GetMapping("reservation/{reservationId}")
    @Operation(summary = "Lister les paiements d'une réservation")
    @ApiResponse(responseCode = "200", description = "Paiements de la réservation récupérés")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByReservation(@PathVariable Long reservationId) {
        List<PaymentResponse> payments = paymentUseCase.obtenirPaymentsParReservation(reservationId)
                .stream()
                .map(PaymentResponse::from)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    @ApiResponse(responseCode = "201", description = "Paiement créé avec succès")
    @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la création")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentCreateRequest request) {
        Payment payment = new Payment();
        payment.setReservationId(request.reservationId());
        payment.setAmount(request.amount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        Payment created = paymentUseCase.creerPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(created));
    }

    @PutMapping("{id}")
    @Operation(summary = "Modifier un paiement existant")
    @ApiResponse(responseCode = "200", description = "Paiement modifié avec succès")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la modification")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable Long id, @RequestBody PaymentUpdateRequest request) {
        Payment payment = paymentUseCase.obtenirPaymentParId(id);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.valueOf(request.status()));

        Payment updated = paymentUseCase.modifierPayment(id, payment);
        return ResponseEntity.ok(PaymentResponse.from(updated));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Supprimer un paiement")
    @ApiResponse(responseCode = "204", description = "Paiement supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la suppression")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentUseCase.supprimerPayment(id);
        return ResponseEntity.noContent().build();
    }
}
