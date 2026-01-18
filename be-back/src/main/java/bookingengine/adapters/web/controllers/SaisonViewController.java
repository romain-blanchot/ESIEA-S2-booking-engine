package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.SaisonDto;
import bookingengine.domain.entities.Saison;
import bookingengine.usecase.saison.SaisonUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/saisons")
public class SaisonViewController {

    private final SaisonUseCase saisonUseCase;

    public SaisonViewController(SaisonUseCase saisonUseCase) {
        this.saisonUseCase = saisonUseCase;
    }

    @GetMapping
    public String listSaisons(Model model) {
        List<Saison> saisons = saisonUseCase.obtenirToutesSaisons();
        model.addAttribute("saisons", saisons);
        return "saison/list";
    }

    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("saison", new SaisonDto(null, "", null, null, 1.0));
        return "saison/form";
    }

    @GetMapping("/{id}/modifier")
    public String showEditForm(@PathVariable Long id, Model model) {
        Saison saison = saisonUseCase.obtenirSaison(id);
        model.addAttribute("saison", SaisonDto.from(saison));
        return "saison/form";
    }

    @PostMapping("/sauvegarder")
    public String saveSaison(@ModelAttribute SaisonDto saisonDto) {
        if (saisonDto.id() == null) {
            saisonUseCase.creerSaison(saisonDto.toDomain());
        } else {
            saisonUseCase.modifierSaison(saisonDto.id(), saisonDto.toDomain());
        }
        return "redirect:/saisons";
    }

    @PostMapping("/{id}/supprimer")
    public String deleteSaison(@PathVariable Long id) {
        saisonUseCase.supprimerSaison(id);
        return "redirect:/saisons";
    }
}
