package bookingengine.usecase.prix;

import bookingengine.domain.entities.Chambre;
import bookingengine.domain.entities.Saison;
import bookingengine.domain.repositories.ChambreRepository;
import bookingengine.domain.repositories.SaisonRepository;
import bookingengine.domain.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CalculPrixUseCase {

    private final ChambreRepository chambreRepository;
    private final SaisonRepository saisonRepository;

    public CalculPrixUseCase(ChambreRepository chambreRepository, SaisonRepository saisonRepository) {
        this.chambreRepository = chambreRepository;
        this.saisonRepository = saisonRepository;
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
        double prixTotal = calculerPrix(chambreId, dateDebut, dateFin);

        return new ResultatCalculPrix(
                chambre.getNumero(),
                chambre.getType(),
                dateDebut,
                dateFin,
                nombreNuits,
                chambre.getPrixBase(),
                prixTotal
        );
    }

    public record ResultatCalculPrix(
            String numeroChambre,
            String typeChambre,
            LocalDate dateDebut,
            LocalDate dateFin,
            long nombreNuits,
            double prixBaseParNuit,
            double prixTotal
    ) {}
}
