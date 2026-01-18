package bookingengine.adapters.web.dto;

import java.time.LocalDate;

public record ReservationUpdateRequest(
        LocalDate dateDebut,
        LocalDate dateFin,
        String status
) {}
