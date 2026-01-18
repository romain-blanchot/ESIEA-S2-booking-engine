package bookingengine.adapters.web.controllers;

import bookingengine.adapters.web.dto.ChambreDto;
import bookingengine.domain.entities.Chambre;
import bookingengine.usecase.chambre.ChambreUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chambres")
public class ChambreViewController {

    private final ChambreUseCase chambreUseCase;

    public ChambreViewController(ChambreUseCase chambreUseCase) {
        this.chambreUseCase = chambreUseCase;
    }

    @GetMapping
    public String listChambres(Model model) {
        List<Chambre> chambres = chambreUseCase.obtenirToutesChambres();
        model.addAttribute("chambres", chambres);
        return "chambre/list";
    }

    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("chambre", new ChambreDto(null, "", "", 0.0, 1, "", true));
        return "chambre/form";
    }

    @GetMapping("/{id}/modifier")
    public String showEditForm(@PathVariable Long id, Model model) {
        Chambre chambre = chambreUseCase.obtenirChambre(id);
        model.addAttribute("chambre", ChambreDto.from(chambre));
        return "chambre/form";
    }

    @PostMapping("/sauvegarder")
    public String saveChambre(@ModelAttribute ChambreDto chambreDto) {
        if (chambreDto.id() == null) {
            chambreUseCase.creerChambre(chambreDto.toDomain());
        } else {
            chambreUseCase.modifierChambre(chambreDto.id(), chambreDto.toDomain());
        }
        return "redirect:/chambres";
    }

    @PostMapping("/{id}/supprimer")
    public String deleteChambre(@PathVariable Long id) {
        chambreUseCase.supprimerChambre(id);
        return "redirect:/chambres";
    }
}
