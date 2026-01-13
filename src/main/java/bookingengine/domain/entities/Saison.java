package bookingengine.domain.entities;

import java.time.LocalDate;

public class Saison {
    private Long id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double coefficientPrix;

    public Saison() {}

    public Saison(Long id, String nom, LocalDate dateDebut, LocalDate dateFin, double coefficientPrix) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.coefficientPrix = coefficientPrix;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public double getCoefficientPrix() { return coefficientPrix; }
    public void setCoefficientPrix(double coefficientPrix) { this.coefficientPrix = coefficientPrix; }
}
