// src/main/java/com/example/payment/repository/PaymentRepository.java
package com.example.payment.repository;

import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.reservation.model.Reservation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    Page<Payment> findByReservation(Reservation reservation, Pageable pageable);
}
