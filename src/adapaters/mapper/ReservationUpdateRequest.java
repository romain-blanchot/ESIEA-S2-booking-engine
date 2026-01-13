// src/main/java/com/example/reservation/dto/ReservationUpdateRequest.java
package com.example.reservation.dto;

import com.example.reservation.model.ReservationStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationUpdateRequest {

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

    @NotNull
    private ReservationStatus status;

    // getters / setters
}
