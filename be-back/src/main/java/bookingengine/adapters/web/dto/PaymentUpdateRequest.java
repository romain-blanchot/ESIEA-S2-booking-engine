package bookingengine.adapters.web.dto;

public record PaymentUpdateRequest(
        String paymentMethod,
        String status
) {}
