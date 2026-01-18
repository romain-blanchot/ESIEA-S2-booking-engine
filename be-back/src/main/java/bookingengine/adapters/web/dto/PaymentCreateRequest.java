package bookingengine.adapters.web.dto;

import java.math.BigDecimal;

public record PaymentCreateRequest(
        Long reservationId,
        BigDecimal amount,
        String paymentMethod
) {}
