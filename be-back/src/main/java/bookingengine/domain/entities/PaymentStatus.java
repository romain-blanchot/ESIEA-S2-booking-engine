package bookingengine.domain.entities;

public enum PaymentStatus {
    PENDING,      // En attente de paiement
    CONFIRMED,    // Paiement confirme
    CANCELLED,    // Paiement annule
    REFUNDED      // Paiement rembourse
}
