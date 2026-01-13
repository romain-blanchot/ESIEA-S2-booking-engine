package bookingengine.adapters.web.dto;

public record InscriptionRequest(
        String username,
        String password,
        String email
) {}
