// src/main/java/com/example/payment/dto/PaymentCreateRequest.java
package com.example.payment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentCreateRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 50)
    private String paymentMethod;

    @NotNull
    private LocalDateTime paymentDate;

    // getters / setters
}
