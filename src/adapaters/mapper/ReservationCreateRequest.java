// src/main/java/com/example/reservation/dto/ReservationCreateRequest.java
package com.example.reservation.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationCreateRequest {

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalAmount;

    @NotBlank
    @Size(max = 50)
    private String paymentMethod;

    // getters / setters
}
