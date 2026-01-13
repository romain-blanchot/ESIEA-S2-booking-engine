// src/main/java/com/example/payment/controller/PaymentController.java
package com.example.payment.controller;

import com.example.payment.dto.*;
import com.example.payment.model.*;
import com.example.payment.repository.PaymentRepository;
import com.example.reservation.model.Reservation;
import com.example.reservation.repository.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentController(PaymentRepository paymentRepository,
                             ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public Page<PaymentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Long reservationId
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());

        Page<Payment> result;
        if (reservationId != null) {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElse(null);
            if (reservation == null) {
                return Page.empty(pageable);
            }
            result = paymentRepository.findByReservation(reservation, pageable);
        } else if (status != null) {
            result = paymentRepository.findByStatus(status, pageable);
        } else {
            result = paymentRepository.findAll(pageable);
        }

        return result.map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody PaymentCreateRequest request
    ) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Reservation not found");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(request.getPaymentDate());

        Payment saved = paymentRepository.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentUpdateRequest request
    ) {
        return paymentRepository.findById(id)
                .map(existing -> {
                    existing.setAmount(request.getAmount());
                    existing.setPaymentMethod(request.getPaymentMethod());
                    existing.setStatus(request.getStatus());
                    existing.setPaymentDate(request.getPaymentDate());
                    Payment updated = paymentRepository.save(existing);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        dto.setReservationId(payment.getReservation().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}
