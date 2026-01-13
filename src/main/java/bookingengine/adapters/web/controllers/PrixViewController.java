package bookingengine.adapters.web.controllers;

import bookingengine.domain.entities.Chambre;
import bookingengine.usecase.chambre.ChambreUseCase;
import bookingengine.usecase.prix.CalculPrixUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prix")
public class PrixViewController {

    private final CalculPrixUseCase calculPrixUseCase;
    private final ChambreUseCase chambreUseCase;

    public PrixViewController(CalculPrixUseCase calculPrixUseCase, ChambreUseCase chambreUseCase) {
        this.calculPrixUseCase = calculPrixUseCase;
        this.chambreUseCase = chambreUseCase;
    }

    @GetMapping
    public String showCalculForm(Model model) {
        List<Chambre> chambres = chambreUseCase.obtenirToutesChambres();
        model.addAttribute("chambres", chambres);
        model.addAttribute("resultat", null);
        return "prix/calcul";
    }

    @PostMapping("/calculer")
    public String calculerPrix(
            @RequestParam Long chambreId,
            @RequestParam LocalDate dateDebut,
            @RequestParam LocalDate dateFin,
            Model model) {

        List<Chambre> chambres = chambreUseCase.obtenirToutesChambres();
        model.addAttribute("chambres", chambres);

        CalculPrixUseCase.ResultatCalculPrix resultat = calculPrixUseCase.calculerPrixDetaille(
                chambreId, dateDebut, dateFin);
        model.addAttribute("resultat", resultat);
        model.addAttribute("chambreId", chambreId);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        return "prix/calcul";
    }
}
