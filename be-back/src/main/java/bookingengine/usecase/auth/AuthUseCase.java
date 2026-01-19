package bookingengine.usecase.auth;

import bookingengine.domain.entities.Utilisateur;
import bookingengine.domain.ports.PasswordEncoderPort;
import bookingengine.domain.repositories.UtilisateurRepository;

public class AuthUseCase {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoderPort passwordEncoder;

    public AuthUseCase(UtilisateurRepository utilisateurRepository, PasswordEncoderPort passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilisateur inscrire(String username, String password, String email) {
        if (utilisateurRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Le nom d'utilisateur existe déjà");
        }
        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("L'email existe déjà");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setEmail(email);
        utilisateur.setRole("USER");

        return utilisateurRepository.save(utilisateur);
    }

    public boolean verifierMotDePasse(String username, String password) {
        return utilisateurRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}
