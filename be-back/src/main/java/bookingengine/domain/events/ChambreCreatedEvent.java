package bookingengine.domain.events;

import java.time.Instant;

public record ChambreCreatedEvent(
        Long chambreId,
        String numero,
        String type,
        double prixBase,
        Instant timestamp
) {
    public static ChambreCreatedEvent of(Long chambreId, String numero, String type, double prixBase) {
        return new ChambreCreatedEvent(chambreId, numero, type, prixBase, Instant.now());
    }
}
