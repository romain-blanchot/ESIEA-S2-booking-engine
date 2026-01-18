package bookingengine.adapters.web.dto;

public record ChambreDto(
        Long id,
        String numero,
        String type,
        double prixBase,
        int capacite,
        String description,
        boolean disponible
) {
    public static ChambreDto from(bookingengine.domain.entities.Chambre chambre) {
        return new ChambreDto(
                chambre.getId(),
                chambre.getNumero(),
                chambre.getType(),
                chambre.getPrixBase(),
                chambre.getCapacite(),
                chambre.getDescription(),
                chambre.isDisponible()
        );
    }

    public bookingengine.domain.entities.Chambre toDomain() {
        return new bookingengine.domain.entities.Chambre(id, numero, type, prixBase, capacite, description, disponible);
    }
}
