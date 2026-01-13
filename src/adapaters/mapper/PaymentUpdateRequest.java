// src/main/java/com/example/payment/dto/PaymentUpdateRequest.java
package com.example.payment.dto;

import com.example.payment.model.PaymentStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentUpdateRequest {

    @NotNull
    private BigDecimal amount;

    @NotBlank
    @Size(max = 50)
    private String paymentMethod;

    @NotNull
    private PaymentStatus status;

    @NotNull
    private LocalDateTime paymentDate;

    // getters / setters
}
