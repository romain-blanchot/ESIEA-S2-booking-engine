package bookingengine.usecase.prix;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.entities.Saison;
import bookingengine.domain.events.PrixCalculatedEvent;
import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.SaisonRepository;
import bookingengine.domain.exceptions.EntityNotFoundException;
import bookingengine.frameworks.kafka.EventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculPrixUseCase {

    private final ChambreRepository chambreRepository;
    private final SaisonRepository saisonRepository;
    private final EventPublisher eventPublisher;

    public CalculPrixUseCase(ChambreRepository chambreRepository, SaisonRepository saisonRepository, EventPublisher eventPublisher) {
        this.chambreRepository = chambreRepository;
        this.saisonRepository = saisonRepository;
        this.eventPublisher = eventPublisher;
    }

    public double calculerPrix(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        Chambre chambre = chambreRepository.findById(chambreId)
                .orElseThrow(() -> new EntityNotFoundException("Chambre non trouvée avec l'id: " + chambreId));

        long nombreNuits = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (nombreNuits <= 0) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        double prixTotal = 0;
        LocalDate dateActuelle = dateDebut;

        while (dateActuelle.isBefore(dateFin)) {
            double prixNuit = chambre.getPrixBase();

            Saison saison = saisonRepository.findByDate(dateActuelle).orElse(null);
            if (saison != null) {
                prixNuit *= saison.getCoefficientPrix();
            }

            prixTotal += prixNuit;
            dateActuelle = dateActuelle.plusDays(1);
        }

        return Math.round(prixTotal * 100.0) / 100.0;
    }

    public ResultatCalculPrix calculerPrixDetaille(Long chambreId, LocalDate dateDebut, LocalDate dateFin) {
        Chambre chambre = chambreRepository.findById(chambreId)
                .orElseThrow(() -> new EntityNotFoundException("Chambre non trouvée avec l'id: " + chambreId));

        long nombreNuits = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (nombreNuits <= 0) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        List<DetailJour> detailsParJour = new ArrayList<>();
        double prixTotal = 0;
        double sommeCoefficients = 0;
        LocalDate dateActuelle = dateDebut;

        while (dateActuelle.isBefore(dateFin)) {
            double prixNuit = chambre.getPrixBase();
            double coefficient = 1.0;
            String nomSaison = "Hors saison";

            Saison saison = saisonRepository.findByDate(dateActuelle).orElse(null);
            if (saison != null) {
                coefficient = saison.getCoefficientPrix();
                nomSaison = saison.getNom();
                prixNuit *= coefficient;
            }

            detailsParJour.add(new DetailJour(
                    dateActuelle,
                    nomSaison,
                    coefficient,
                    Math.round(prixNuit * 100.0) / 100.0
            ));

            prixTotal += prixNuit;
            sommeCoefficients += coefficient;
            dateActuelle = dateActuelle.plusDays(1);
        }

        double coefficientMoyen = Math.round((sommeCoefficients / nombreNuits) * 100.0) / 100.0;
        prixTotal = Math.round(prixTotal * 100.0) / 100.0;

        // Publier l'événement Kafka
        eventPublisher.publish(PrixCalculatedEvent.of(
                chambreId, chambre.getNumero(), chambre.getType(),
                dateDebut, dateFin, nombreNuits, prixTotal));

        return new ResultatCalculPrix(
                chambre.getNumero(),
                chambre.getType(),
                dateDebut,
                dateFin,
                nombreNuits,
                chambre.getPrixBase(),
                coefficientMoyen,
                prixTotal,
                detailsParJour
        );
    }

    public record DetailJour(
            LocalDate date,
            String saison,
            double coefficient,
            double prix
    ) {}

    public record ResultatCalculPrix(
            String numeroChambre,
            String typeChambre,
            LocalDate dateDebut,
            LocalDate dateFin,
            long nombreNuits,
            double prixBaseParNuit,
            double coefficientSaisonnier,
            double prixTotal,
            List<DetailJour> detailsParJour
    ) {}
}
