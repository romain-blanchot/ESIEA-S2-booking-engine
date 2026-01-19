# Hotel Booking Engine

Moteur de reservation hoteliere avec backend Spring Boot (Clean Architecture) et frontend Next.js.

## Demarrage rapide

```bash
# Lancer tous les services
docker compose -f compose.yaml up --build

# Arreter et reinitialiser
docker compose -f compose.yaml down -v
```

L'application sera accessible sur http://localhost

## Identifiants de demo

### Compte Administrateur
| Champ | Valeur |
|-------|--------|
| Username | `admin` |
| Password | `admin123` |
| Email | admin@hotel-spa.fr |
| Role | ADMIN |

### Compte Utilisateur
| Champ | Valeur |
|-------|--------|
| Username | `user` |
| Password | `user123` |
| Email | user@example.com |
| Role | USER |

### Autres comptes de test
| Username | Password | Email |
|----------|----------|-------|
| marie.dupont | password | marie.dupont@email.com |
| jean.martin | password | jean.martin@email.com |

## Donnees de demo

Au demarrage, la base de donnees est automatiquement peuplee avec :

- **4 utilisateurs** (1 admin + 3 users)
- **10 chambres** (Simple, Double, Suite, Familiale)
- **6 saisons tarifaires** (avec coefficients 0.8 a 1.8)
- **5 reservations** (differents statuts)
- **5 paiements** (differents statuts)

## Architecture

```
                      +-------------------+
                      |      Nginx        |
                      |    (port 80)      |
                      +---------+---------+
                                |
                +---------------+---------------+
                |                               |
                v                               v
      +-------------------+           +-------------------+
      |    Frontend       |           |    Backend        |
      |   Next.js         |           |   Spring Boot     |
      |   (port 3000)     |           |   (port 8080)     |
      +-------------------+           +-------------------+
                                              |
                          +-------------------+-------------------+
                          |                                       |
                          v                                       v
                  +---------------+                       +---------------+
                  |  PostgreSQL   |                       |    Kafka      |
                  |  (port 5432)  |                       |  (port 9092)  |
                  +---------------+                       +---------------+
```

## URLs d'acces

| URL | Description |
|-----|-------------|
| http://localhost/ | Frontend - Page d'accueil |
| http://localhost/admin | Frontend - Administration |
| http://localhost/auth/connexion | Frontend - Connexion |
| http://localhost/auth/inscription | Frontend - Inscription |
| http://localhost/swagger-ui.html | Swagger UI - Documentation API |
| http://localhost:8080 | Kafka UI |

## API Endpoints

### Authentification
- `POST /auth/inscription` - Inscription
- `POST /auth/connexion` - Connexion

### Chambres
- `GET /chambres` - Liste des chambres
- `GET /chambres/{id}` - Detail d'une chambre
- `GET /chambres/disponibles` - Chambres disponibles
- `GET /chambres/type/{type}` - Chambres par type
- `POST /chambres` - Creer une chambre
- `PUT /chambres/{id}` - Modifier une chambre
- `DELETE /chambres/{id}` - Supprimer une chambre

### Saisons
- `GET /saisons` - Liste des saisons
- `GET /saisons/{id}` - Detail d'une saison
- `POST /saisons` - Creer une saison
- `PUT /saisons/{id}` - Modifier une saison
- `DELETE /saisons/{id}` - Supprimer une saison

### Reservations
- `GET /reservations` - Liste des reservations
- `GET /reservations/{id}` - Detail d'une reservation
- `GET /reservations/status/{status}` - Par statut
- `GET /reservations/chambre/{id}` - Par chambre
- `GET /reservations/utilisateur/{id}` - Par utilisateur
- `POST /reservations` - Creer une reservation
- `PUT /reservations/{id}` - Modifier une reservation
- `PUT /reservations/{id}/cancel` - Annuler une reservation
- `DELETE /reservations/{id}` - Supprimer une reservation

### Paiements
- `GET /payments` - Liste des paiements
- `GET /payments/{id}` - Detail d'un paiement
- `GET /payments/reservation/{id}` - Par reservation
- `POST /payments` - Creer un paiement
- `PUT /payments/{id}` - Modifier un paiement
- `DELETE /payments/{id}` - Supprimer un paiement

### Prix
- `POST /prix/calculer` - Calculer le prix d'un sejour

## Stack technique

### Backend
- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- Springdoc OpenAPI (Swagger)

### Frontend
- Next.js 16
- React 19
- TypeScript
- Tailwind CSS v4

### Infrastructure
- Docker & Docker Compose
- Nginx (reverse proxy)
- Kafka (message broker)
- PostgreSQL (base de donnees)

## Clean Architecture

```
be-back/src/main/java/bookingengine/
|
+-- domain/              # Pure Java - Aucune dependance framework
|   +-- entities/        # Entites metier
|   +-- repositories/    # Interfaces repositories
|   +-- ports/           # Ports (EventPublisherPort, PasswordEncoderPort)
|   +-- events/          # Evenements domaine
|   +-- exceptions/      # Exceptions metier
|
+-- usecase/             # Pure Java - Logique metier
|   +-- auth/            # AuthUseCase
|   +-- chambre/         # ChambreUseCase
|   +-- saison/          # SaisonUseCase
|   +-- reservation/     # ReservationUseCase
|   +-- payment/         # PaymentUseCase
|   +-- prix/            # CalculPrixUseCase
|
+-- adapters/            # Spring autorise
|   +-- persistence/     # JPA entities, mappers, repositories impl
|   +-- web/             # Controllers REST, DTOs
|
+-- frameworks/          # Configuration Spring
    +-- config/          # UseCaseConfig, DataSeeder
    +-- security/        # SecurityConfig, PasswordEncoderAdapter
    +-- kafka/           # KafkaConfig, EventPublisher, EventListener
```

## Developpement

### Backend seul
```bash
cd be-back
./mvnw spring-boot:run    # Demarre Postgres & Kafka automatiquement
./mvnw test               # Lance les tests
```

### Frontend seul
```bash
cd be-front
pnpm install
pnpm dev                  # Serveur de developpement
pnpm build                # Build production
```

## Projet ESIEA S2

Moteur de reservation hoteliere - Architecture Clean + Event-Driven
