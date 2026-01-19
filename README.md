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
| http://localhost/mon-compte | Frontend - Espace utilisateur (reservations) |
| http://localhost/admin | Frontend - Administration (ADMIN uniquement) |
| http://localhost/connexion | Frontend - Connexion |
| http://localhost/inscription | Frontend - Inscription |
| http://localhost/swagger-ui.html | Swagger UI - Documentation API |
| http://localhost:8080 | Kafka UI |

## Kafka UI - Monitoring des evenements

L'application utilise Kafka pour publier des evenements metier. Kafka UI permet de visualiser ces evenements en temps reel.

### Acces
- URL : http://localhost:8080
- Cluster : `local`

### Topics disponibles

| Topic | Description | Evenements |
|-------|-------------|------------|
| `booking.chambres` | Evenements chambres | ChambreCreatedEvent |
| `booking.saisons` | Evenements saisons | SaisonCreatedEvent |
| `booking.reservations` | Evenements reservations | ReservationCreatedEvent, ReservationCancelledEvent |
| `booking.payments` | Evenements paiements | PaymentCreatedEvent, PaymentStatusChangedEvent |
| `booking.prix` | Evenements calcul prix | PrixCalculatedEvent |

### Liste des 7 evenements domaine

| Evenement | Declencheur | Donnees |
|-----------|-------------|---------|
| ChambreCreatedEvent | Creation chambre | chambreId, numero, type, prixBase |
| SaisonCreatedEvent | Creation saison | saisonId, nom, dateDebut, dateFin, coefficient |
| ReservationCreatedEvent | Nouvelle reservation | reservationId, chambreId, utilisateurId, dates, status |
| ReservationCancelledEvent | Annulation reservation | reservationId, reason |
| PaymentCreatedEvent | Creation paiement | paymentId, reservationId, amount, method, status |
| PaymentStatusChangedEvent | Changement statut paiement | paymentId, oldStatus, newStatus |
| PrixCalculatedEvent | Calcul de prix | chambreId, numeroChambre, dates, nombreNuits, prixTotal |

### Visualiser les messages

1. Ouvrir http://localhost:8080
2. Cliquer sur **Topics** dans le menu gauche
3. Selectionner un topic (ex: `booking.payments`)
4. Cliquer sur l'onglet **Messages**
5. Les evenements JSON apparaissent avec leur timestamp

### Exemples d'evenements

**PaymentStatusChanged** (topic: `booking.payments`)
```json
{
  "paymentId": 2,
  "oldStatus": "PENDING",
  "newStatus": "CONFIRMED",
  "timestamp": 1768854685.632
}
```

**ReservationCreated** (topic: `booking.reservations`)
```json
{
  "reservationId": 6,
  "chambreId": 5,
  "utilisateurId": 1,
  "dateDebut": [2026, 3, 1],
  "dateFin": [2026, 3, 5],
  "status": "PENDING",
  "timestamp": 1768854698.592
}
```

**PrixCalculated** (topic: `booking.prix`)
```json
{
  "chambreId": 1,
  "numeroChambre": "101",
  "typeChambre": "Simple",
  "dateDebut": [2026, 2, 1],
  "dateFin": [2026, 2, 3],
  "nombreNuits": 2,
  "prixTotal": 142.4,
  "timestamp": 1768854536.710
}
```

### Tester les evenements

1. Effectuer une action dans l'application (ex: changer le statut d'un paiement dans /admin)
2. Rafraichir la vue Messages du topic correspondant dans Kafka UI
3. Le nouvel evenement apparait en haut de la liste

## API Endpoints

### Authentification
- `POST /auth/inscription` - Inscription
- `POST /auth/connexion` - Connexion

### Chambres
- `GET /chambres` - Liste des chambres
- `GET /chambres/{id}` - Detail d'une chambre
- `GET /chambres/disponibles` - Chambres disponibles (flag disponible=true)
- `GET /chambres/type/{type}` - Chambres par type
- `GET /chambres/{id}/disponibilite?dateDebut&dateFin` - Verifier disponibilite pour dates
- `GET /chambres/disponibles-periode?dateDebut&dateFin` - Chambres libres pour une periode
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

## Regles metier

### Disponibilite des chambres

Il y a **deux niveaux** de disponibilite pour une chambre :

1. **Flag `disponible`** (administratif)
   - `true` = chambre a la vente
   - `false` = chambre hors service (maintenance, renovation...)
   - Une chambre avec `disponible=false` ne peut pas etre reservee

2. **Conflits de dates** (dynamique)
   - Verifie si la chambre est deja reservee pour les dates demandees
   - Une chambre peut avoir plusieurs reservations sur des periodes differentes

### Flux de reservation

```
Client demande reservation
        |
        v
[Chambre existe?] --Non--> Erreur 404
        |
       Oui
        v
[Chambre disponible?] --Non--> Erreur 409 "Chambre hors service"
        |
       Oui
        v
[Dates libres?] --Non--> Erreur 409 "Deja reservee"
        |
       Oui
        v
Reservation creee (PENDING)
        |
        v
Paiement auto-cree (PENDING)
```

### Statuts

**ReservationStatus** : PENDING → CONFIRMED → COMPLETED | CANCELLED

**PaymentStatus** : PENDING → CONFIRMED | CANCELLED | REFUNDED

**Integration** : Quand un paiement passe a CONFIRMED, la reservation passe automatiquement a CONFIRMED.

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
