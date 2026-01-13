// src/main/java/com/example/reservation/repository/ReservationRepository.java
package com.example.reservation.repository;

import com.example.reservation.model.Reservation;
import com.example.reservation.model.ReservationStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
