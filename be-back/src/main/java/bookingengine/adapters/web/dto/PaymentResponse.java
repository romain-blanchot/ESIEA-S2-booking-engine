package bookingengine.adapters.web.dto;

import bookingengine.domain.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long reservationId,
        BigDecimal amount,
        String paymentMethod,
        String status,
        LocalDateTime paymentDate
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus().name(),
                payment.getPaymentDate()
        );
    }
}
