// src/main/java/com/example/reservation/controller/ReservationController.java
package com.example.reservation.controller;

import com.example.reservation.dto.*;
import com.example.reservation.model.*;
import com.example.reservation.repository.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationRepository repository;

    public ReservationController(ReservationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<ReservationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ReservationStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Reservation> result = (status != null)
                ? repository.findByStatus(status, pageable)
                : repository.findAll(pageable);

        return result.map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(r -> ResponseEntity.ok(toResponse(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            return ResponseEntity.badRequest().body("startDate must be before or equal to endDate");
        }

        Reservation reservation = new Reservation();
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalAmount(request.getTotalAmount());
        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setCancelledAt(null);

        Reservation saved = repository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            return ResponseEntity.badRequest().body("startDate must be before or equal to endDate");
        }

        return repository.findById(id)
                .map(existing -> {
                    existing.setStartDate(request.getStartDate());
                    existing.setEndDate(request.getEndDate());
                    existing.setTotalAmount(request.getTotalAmount());
                    existing.setPaymentMethod(request.getPaymentMethod());
                    existing.setStatus(request.getStatus());

                    if (request.getStatus() == ReservationStatus.CANCELLED
                            && existing.getCancelledAt() == null) {
                        existing.setCancelledAt(LocalDateTime.now());
                    }
                    Reservation updated = repository.save(existing);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id); // suppression physique
        return ResponseEntity.noContent().build();
    }

    private ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse dto = new ReservationResponse();
        dto.setId(reservation.getId());
        dto.setStartDate(reservation.getStartDate());
        dto.setEndDate(reservation.getEndDate());
        dto.setStatus(reservation.getStatus());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setPaymentMethod(reservation.getPaymentMethod());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setCancelledAt(reservation.getCancelledAt());
        return dto;
    }
}
