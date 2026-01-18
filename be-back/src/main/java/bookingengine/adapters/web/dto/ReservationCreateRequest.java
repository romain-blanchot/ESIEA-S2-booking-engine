package bookingengine.adapters.web.dto;

import java.time.LocalDate;

public record ReservationCreateRequest(
        Long chambreId,
        Long utilisateurId,
        LocalDate dateDebut,
        LocalDate dateFin
) {}
