package bookingengine.adapters.web.dto;

import java.time.LocalDate;

public record CalculPrixRequest(
        Long chambreId,
        LocalDate dateDebut,
        LocalDate dateFin
) {}
