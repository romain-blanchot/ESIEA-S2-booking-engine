# Booking Engine

Booking Engine Spring Boot (BLANCHOT, ATTOY, ALLAM)

## Prérequis

- Java 21+
- Docker & Docker Compose
- Maven (ou utiliser le wrapper `./mvnw`)

## Lancement rapide

### 1. Cloner le repository

```bash
git clone <url-du-repo>
cd ESIEA-S2-booking-engine
```

### 2. Lancer l'application (Docker Compose)

```bash
docker compose up --build
```

Cela lance tous les services : PostgreSQL, Kafka, Backend, Frontend et Nginx (reverse proxy).

### 3. Accéder aux services

| Service | URL |
|---------|-----|
| Application | http://localhost |
| API REST | http://localhost/api |
| Swagger UI | http://localhost/swagger-ui.html |
| OpenAPI JSON | http://localhost/v3/api-docs |
| Kafka UI | http://localhost:8080 |

### 4. Développement local (Backend seul)

```bash
cd be-back
./mvnw spring-boot:run
```

Spring Boot lance automatiquement les conteneurs Docker (PostgreSQL et Kafka) grâce à `spring-boot-docker-compose`.

### 5. Authentification

Pour accéder aux endpoints protégés :
- **Utilisateur** : `user`
- **Mot de passe** : `password`

Ou créer un compte via l'API :
```bash
curl -X POST http://localhost/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{"username":"monuser","password":"monpassword","email":"email@example.com"}'
```

## Architecture Clean Architecture

Le projet suit les principes SOLID avec une architecture en couches :

```
src/main/java/bookingengine/
├── domain/                          # Couche Domaine (Entités métier)
│   ├── entities/
│   │   ├── Saison.java              # Saison tarifaire
│   │   ├── Chambre.java             # Chambre d'hôtel
│   │   └── Utilisateur.java         # Utilisateur
│   ├── repositories/                # Interfaces des repositories
│   ├── ports/                       # Ports (EventPublisher, PasswordEncoder)
│   ├── events/                      # Événements métier
│   └── exceptions/                  # Exceptions métier
│
├── usecase/                         # Couche Use Cases (Logique métier)
│   ├── saison/SaisonUseCase.java    # CRUD Saisons
│   ├── chambre/ChambreUseCase.java  # CRUD Chambres
│   ├── prix/CalculPrixUseCase.java  # Calcul prix avec coefficients
│   └── auth/AuthUseCase.java        # Authentification
│
├── adapters/                        # Couche Adapters
│   ├── persistence/                 # Adaptateurs JPA
│   │   ├── entities/                # Entités JPA
│   │   ├── repositories/            # Spring Data JPA Repositories
│   │   ├── mappers/                 # Mappers Domain <-> JPA
│   │   └── *RepositoryImpl.java     # Implémentations
│   └── web/                         # Adaptateurs Web
│       ├── controllers/             # REST Controllers
│       └── dto/                     # Data Transfer Objects
│
└── frameworks/                      # Couche Frameworks
    ├── config/                      # Configuration UseCases
    ├── security/                    # Spring Security
    └── kafka/                       # Configuration Kafka
```

## Architecture Docker

```
                    ┌─────────────────┐
        :80         │     Nginx       │
   ────────────────>│ (reverse proxy) │
                    └────────┬────────┘
                             │
              ┌──────────────┴──────────────┐
              │                             │
              ▼                             ▼
    ┌─────────────────┐           ┌─────────────────┐
    │    Frontend     │           │    Backend      │
    │   (Next.js)     │           │  (Spring Boot)  │
    │    :3000        │           │    :8080        │
    └─────────────────┘           └────────┬────────┘
                                           │
                          ┌────────────────┼────────────────┐
                          ▼                ▼                ▼
                   ┌───────────┐    ┌───────────┐    ┌───────────┐
                   │ PostgreSQL│    │   Kafka   │    │ Kafka UI  │
                   │   :5432   │    │   :9092   │    │   :8080   │
                   └───────────┘    └───────────┘    └───────────┘
```

## Fonctionnalités

### 1. Gestion des Saisons (CRUD)
- Créer, lire, modifier, supprimer des saisons tarifaires
- Chaque saison a un coefficient de prix (ex: 1.5 = +50% en haute saison)
- **API** : `GET/POST/PUT/DELETE /api/saisons`

### 2. Gestion des Chambres (CRUD)
- Créer, lire, modifier, supprimer des chambres
- Types : SIMPLE, DOUBLE, SUITE, FAMILIALE
- Gestion de la disponibilité
- **API** : `GET/POST/PUT/DELETE /api/chambres`

### 3. Calcul de Prix
- Calcule le prix total d'un séjour
- Applique les coefficients saisonniers jour par jour
- Retourne le détail du calcul
- **API** : `POST /api/prix/calculer`

### 4. Authentification
- Inscription d'utilisateurs
- Connexion via Spring Security
- **API** : `POST /api/auth/inscription`

## API REST (Swagger)

Accéder à la documentation interactive : http://localhost/swagger-ui.html

### Exemples d'utilisation

**Créer une saison :**
```bash
curl -X POST http://localhost/api/saisons \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Haute Saison",
    "dateDebut": "2024-07-01",
    "dateFin": "2024-08-31",
    "coefficientPrix": 1.5
  }'
```

**Créer une chambre :**
```bash
curl -X POST http://localhost/api/chambres \
  -H "Content-Type: application/json" \
  -d '{
    "numero": "101",
    "type": "DOUBLE",
    "prixBase": 80.0,
    "capacite": 2,
    "description": "Chambre double avec vue mer",
    "disponible": true
  }'
```

**Calculer le prix d'un séjour :**
```bash
curl -X POST http://localhost/api/prix/calculer \
  -H "Content-Type: application/json" \
  -d '{
    "chambreId": 1,
    "dateDebut": "2024-07-15",
    "dateFin": "2024-07-20"
  }'
```

## Stack technique

- **Backend** : Spring Boot 4.0.1, Spring Data JPA, Spring Security
- **Frontend** : Next.js 16, React 19, TypeScript, Tailwind CSS
- **Base de données** : PostgreSQL
- **Messaging** : Apache Kafka
- **Reverse Proxy** : Nginx
- **Documentation API** : Springdoc OpenAPI 3.0.1 (Swagger)
- **Architecture** : Clean Architecture

## Docker Compose

Les services sont définis dans `compose.yaml` :

| Service | Port | Description |
|---------|------|-------------|
| nginx | 80 | Reverse proxy (point d'entrée) |
| frontend | 3000 | Next.js (interne) |
| backend | 8080 | Spring Boot API (interne) |
| postgres | 5432 | Base de données |
| kafka | 9092 | Message broker |
| kafka-ui | 8080 | Interface Kafka |

### Commandes Docker

**Lancer tous les services :**
```bash
docker compose up --build
```

**Lancer en arrière-plan :**
```bash
docker compose up -d --build
```

**Arrêter les services :**
```bash
docker compose down
```

**Reset complet (supprime les données) :**
```bash
docker compose down -v
docker compose up --build
```

**Voir les logs :**
```bash
docker compose logs -f backend
docker compose logs -f nginx
```

## Configuration

Le projet est **prêt à l'emploi** sans configuration supplémentaire :

- **Base de données** : Créée automatiquement par Docker Compose
- **Schéma SQL** : Généré automatiquement par Hibernate (`ddl-auto=update`)
- **Tables de session** : Créées automatiquement (`initialize-schema=always`)

Aucun fichier `.env`, aucune variable d'environnement, aucune configuration manuelle requise.

## Tests

```bash
cd be-back
./mvnw test
```

Les tests utilisent Testcontainers pour créer des instances isolées de PostgreSQL et Kafka.

### Couverture de code

```bash
./mvnw test
# Rapport disponible dans target/site/jacoco/index.html
```
