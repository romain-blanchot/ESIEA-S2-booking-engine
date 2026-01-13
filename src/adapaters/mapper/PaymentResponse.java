// src/main/java/com/example/payment/dto/PaymentResponse.java
package com.example.payment.dto;

import com.example.payment.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    // getters / setters
}
