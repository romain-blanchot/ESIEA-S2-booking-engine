package bookingengine.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private Long chambreId;
    private Long utilisateurId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public Reservation() {}

    public Reservation(Long id, Long chambreId, Long utilisateurId, LocalDate dateDebut, LocalDate dateFin, ReservationStatus status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.chambreId = chambreId;
        this.utilisateurId = utilisateurId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChambreId() {
        return chambreId;
    }

    public void setChambreId(Long chambreId) {
        this.chambreId = chambreId;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
