package bookingengine.frameworks.config;

import bookingengine.adapters.persistence.entities.*;
import bookingengine.adapters.persistence.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UtilisateurJpaRepository utilisateurRepository;
    private final ChambreJpaRepository chambreRepository;
    private final SaisonJpaRepository saisonRepository;
    private final ReservationJpaRepository reservationRepository;
    private final PaymentJpaRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UtilisateurJpaRepository utilisateurRepository,
            ChambreJpaRepository chambreRepository,
            SaisonJpaRepository saisonRepository,
            ReservationJpaRepository reservationRepository,
            PaymentJpaRepository paymentRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.chambreRepository = chambreRepository;
        this.saisonRepository = saisonRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() > 0) {
            logger.info("Database already seeded, skipping...");
            return;
        }

        logger.info("Seeding database with demo data...");

        seedUtilisateurs();
        seedChambres();
        seedSaisons();
        seedReservations();
        seedPayments();

        logger.info("Database seeding completed!");
    }

    private void seedUtilisateurs() {
        logger.info("Seeding utilisateurs...");

        // Admin user
        UtilisateurJpaEntity admin = new UtilisateurJpaEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@hotel-spa.fr");
        admin.setRole("ADMIN");
        utilisateurRepository.save(admin);

        // Regular user
        UtilisateurJpaEntity user = new UtilisateurJpaEntity();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setEmail("user@example.com");
        user.setRole("USER");
        utilisateurRepository.save(user);

        // Additional demo users
        UtilisateurJpaEntity marie = new UtilisateurJpaEntity();
        marie.setUsername("marie.dupont");
        marie.setPassword(passwordEncoder.encode("password"));
        marie.setEmail("marie.dupont@email.com");
        marie.setRole("USER");
        utilisateurRepository.save(marie);

        UtilisateurJpaEntity jean = new UtilisateurJpaEntity();
        jean.setUsername("jean.martin");
        jean.setPassword(passwordEncoder.encode("password"));
        jean.setEmail("jean.martin@email.com");
        jean.setRole("USER");
        utilisateurRepository.save(jean);

        logger.info("Created {} utilisateurs", utilisateurRepository.count());
    }

    private void seedChambres() {
        logger.info("Seeding chambres...");

        List<ChambreJpaEntity> chambres = List.of(
            createChambre("101", "Simple", 89.0, 1,
                "Chambre simple confortable avec lit simple, ideal pour voyageur solo. Vue sur jardin.", true),
            createChambre("102", "Simple", 89.0, 1,
                "Chambre simple avec bureau, parfaite pour les voyages d'affaires.", true),
            createChambre("103", "Simple", 95.0, 1,
                "Chambre simple premium avec balcon privatif.", false),
            createChambre("201", "Double", 129.0, 2,
                "Chambre double elegante avec lit queen-size et salle de bain en marbre.", true),
            createChambre("202", "Double", 129.0, 2,
                "Chambre double vue mer avec terrasse privee.", true),
            createChambre("203", "Double", 139.0, 2,
                "Chambre double deluxe avec jacuzzi privatif.", true),
            createChambre("301", "Suite", 249.0, 3,
                "Suite junior avec salon separe, minibar et vue panoramique.", true),
            createChambre("302", "Suite", 349.0, 4,
                "Suite executive avec deux chambres, salon et salle a manger.", true),
            createChambre("401", "Familiale", 189.0, 4,
                "Chambre familiale spacieuse avec deux lits doubles, ideal pour famille.", true),
            createChambre("402", "Familiale", 209.0, 5,
                "Grande suite familiale avec coin enfants et kitchenette.", true)
        );

        chambreRepository.saveAll(chambres);
        logger.info("Created {} chambres", chambreRepository.count());
    }

    private ChambreJpaEntity createChambre(String numero, String type, double prix, int capacite, String description, boolean disponible) {
        ChambreJpaEntity chambre = new ChambreJpaEntity();
        chambre.setNumero(numero);
        chambre.setType(type);
        chambre.setPrixBase(prix);
        chambre.setCapacite(capacite);
        chambre.setDescription(description);
        chambre.setDisponible(disponible);
        return chambre;
    }

    private void seedSaisons() {
        logger.info("Seeding saisons...");

        int currentYear = LocalDate.now().getYear();

        List<SaisonJpaEntity> saisons = List.of(
            createSaison("Basse Saison Hiver",
                LocalDate.of(currentYear, 1, 7), LocalDate.of(currentYear, 3, 31), 0.8),
            createSaison("Printemps",
                LocalDate.of(currentYear, 4, 1), LocalDate.of(currentYear, 5, 31), 1.0),
            createSaison("Haute Saison Ete",
                LocalDate.of(currentYear, 6, 1), LocalDate.of(currentYear, 8, 31), 1.5),
            createSaison("Automne",
                LocalDate.of(currentYear, 9, 1), LocalDate.of(currentYear, 10, 31), 1.0),
            createSaison("Basse Saison Novembre",
                LocalDate.of(currentYear, 11, 1), LocalDate.of(currentYear, 11, 30), 0.85),
            createSaison("Fetes de Fin d'Annee",
                LocalDate.of(currentYear, 12, 15), LocalDate.of(currentYear + 1, 1, 6), 1.8)
        );

        saisonRepository.saveAll(saisons);
        logger.info("Created {} saisons", saisonRepository.count());
    }

    private SaisonJpaEntity createSaison(String nom, LocalDate debut, LocalDate fin, double coefficient) {
        SaisonJpaEntity saison = new SaisonJpaEntity();
        saison.setNom(nom);
        saison.setDateDebut(debut);
        saison.setDateFin(fin);
        saison.setCoefficientPrix(coefficient);
        return saison;
    }

    private void seedReservations() {
        logger.info("Seeding reservations...");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<ReservationJpaEntity> reservations = List.of(
            // Reservation confirmee pour user (id=2)
            createReservation(1L, 2L, today.plusDays(5), today.plusDays(8),
                ReservationJpaEntity.ReservationStatusJpa.CONFIRMED, now.minusDays(3), null),
            // Reservation en attente pour marie (id=3)
            createReservation(4L, 3L, today.plusDays(10), today.plusDays(14),
                ReservationJpaEntity.ReservationStatusJpa.PENDING, now.minusDays(1), null),
            // Reservation confirmee pour jean (id=4)
            createReservation(7L, 4L, today.plusDays(2), today.plusDays(5),
                ReservationJpaEntity.ReservationStatusJpa.CONFIRMED, now.minusDays(5), null),
            // Reservation terminee
            createReservation(2L, 3L, today.minusDays(10), today.minusDays(7),
                ReservationJpaEntity.ReservationStatusJpa.COMPLETED, now.minusDays(15), null),
            // Reservation annulee
            createReservation(5L, 2L, today.plusDays(20), today.plusDays(23),
                ReservationJpaEntity.ReservationStatusJpa.CANCELLED, now.minusDays(2), now.minusDays(1))
        );

        reservationRepository.saveAll(reservations);
        logger.info("Created {} reservations", reservationRepository.count());
    }

    private ReservationJpaEntity createReservation(Long chambreId, Long utilisateurId,
            LocalDate dateDebut, LocalDate dateFin,
            ReservationJpaEntity.ReservationStatusJpa status,
            LocalDateTime createdAt, LocalDateTime cancelledAt) {
        ReservationJpaEntity reservation = new ReservationJpaEntity();
        reservation.setChambreId(chambreId);
        reservation.setUtilisateurId(utilisateurId);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatus(status);
        reservation.setCreatedAt(createdAt);
        reservation.setCancelledAt(cancelledAt);
        return reservation;
    }

    private void seedPayments() {
        logger.info("Seeding payments...");

        LocalDateTime now = LocalDateTime.now();

        List<PaymentJpaEntity> payments = List.of(
            // Paiement complete pour reservation 1
            createPayment(1L, new BigDecimal("387.00"), "CARTE",
                PaymentJpaEntity.PaymentStatusJpa.CONFIRMED, now.minusDays(3)),
            // Paiement en attente pour reservation 2
            createPayment(2L, new BigDecimal("516.00"), "VIREMENT",
                PaymentJpaEntity.PaymentStatusJpa.PENDING, now.minusDays(1)),
            // Paiement complete pour reservation 3
            createPayment(3L, new BigDecimal("747.00"), "CARTE",
                PaymentJpaEntity.PaymentStatusJpa.CONFIRMED, now.minusDays(5)),
            // Paiement complete pour reservation 4 (terminee)
            createPayment(4L, new BigDecimal("267.00"), "ESPECES",
                PaymentJpaEntity.PaymentStatusJpa.CONFIRMED, now.minusDays(7)),
            // Paiement rembourse pour reservation 5 (annulee)
            createPayment(5L, new BigDecimal("387.00"), "CARTE",
                PaymentJpaEntity.PaymentStatusJpa.REFUNDED, now.minusDays(1))
        );

        paymentRepository.saveAll(payments);
        logger.info("Created {} payments", paymentRepository.count());
    }

    private PaymentJpaEntity createPayment(Long reservationId, BigDecimal amount,
            String paymentMethod, PaymentJpaEntity.PaymentStatusJpa status, LocalDateTime paymentDate) {
        PaymentJpaEntity payment = new PaymentJpaEntity();
        payment.setReservationId(reservationId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(status);
        payment.setPaymentDate(paymentDate);
        return payment;
    }
}
