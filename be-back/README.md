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

### 2. Lancer l'application

```bash
./mvnw spring-boot:run
```

Spring Boot lance automatiquement les conteneurs Docker (PostgreSQL et Kafka) grâce à `spring-boot-docker-compose`.

### 3. Accéder aux services

| Service | URL |
|---------|-----|
| Front-end (Accueil) | http://localhost:8080 |
| Gestion Saisons | http://localhost:8080/saisons |
| Gestion Chambres | http://localhost:8080/chambres |
| Calcul Prix | http://localhost:8080/prix |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### 4. Authentification

Pour accéder aux endpoints protégés :
- **Utilisateur** : `user`
- **Mot de passe** : `password`

Ou créer un compte via l'API :
```bash
curl -X POST http://localhost:8080/api/auth/inscription \
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
│       ├── controllers/             # REST Controllers + Thymeleaf
│       └── dto/                     # Data Transfer Objects
│
└── frameworks/                      # Couche Frameworks
    └── security/SecurityConfig.java # Configuration Spring Security
```

## Fonctionnalités

### 1. Gestion des Saisons (CRUD)
- Créer, lire, modifier, supprimer des saisons tarifaires
- Chaque saison a un coefficient de prix (ex: 1.5 = +50% en haute saison)
- **API** : `GET/POST/PUT/DELETE /api/saisons`
- **Front** : http://localhost:8080/saisons

### 2. Gestion des Chambres (CRUD)
- Créer, lire, modifier, supprimer des chambres
- Types : SIMPLE, DOUBLE, SUITE, FAMILIALE
- Gestion de la disponibilité
- **API** : `GET/POST/PUT/DELETE /api/chambres`
- **Front** : http://localhost:8080/chambres

### 3. Calcul de Prix
- Calcule le prix total d'un séjour
- Applique les coefficients saisonniers jour par jour
- Retourne le détail du calcul
- **API** : `POST /api/prix/calculer`
- **Front** : http://localhost:8080/prix

### 4. Authentification
- Inscription d'utilisateurs
- Connexion via formulaire Spring Security
- **API** : `POST /api/auth/inscription`

## API REST (Swagger)

Accéder à la documentation interactive : http://localhost:8080/swagger-ui.html

### Exemples d'utilisation

**Créer une saison :**
```bash
curl -X POST http://localhost:8080/api/saisons \
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
curl -X POST http://localhost:8080/api/chambres \
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
curl -X POST http://localhost:8080/api/prix/calculer \
  -H "Content-Type: application/json" \
  -d '{
    "chambreId": 1,
    "dateDebut": "2024-07-15",
    "dateFin": "2024-07-20"
  }'
```

## Stack technique

- **Framework** : Spring Boot 4.0.1
- **Base de données** : PostgreSQL
- **Messaging** : Apache Kafka
- **Documentation API** : Springdoc OpenAPI 3.0.1 (Swagger)
- **Front-end** : Thymeleaf + Bootstrap 5
- **Sécurité** : Spring Security
- **Architecture** : Clean Architecture

## Docker Compose

Les services sont définis dans `compose.yaml` :

- **postgres** : Base de données PostgreSQL (port 5432)
- **kafka** : Apache Kafka (port 9092)

### Commandes Docker

**Lancer les conteneurs :**
```bash
docker compose up -d
```

**Arrêter les conteneurs :**
```bash
docker compose down
```

**Reset complet de la base de données :**
```bash
docker compose down -v
docker compose up -d
```
> L'option `-v` supprime les volumes Docker, ce qui efface toutes les données PostgreSQL.

**Voir les logs :**
```bash
docker compose logs -f postgres
```

## Configuration

Le projet est **prêt à l'emploi** sans configuration supplémentaire :

- **Base de données** : Créée automatiquement par Docker Compose
- **Schéma SQL** : Généré automatiquement par Hibernate (`ddl-auto=update`)
- **Tables de session** : Créées automatiquement (`initialize-schema=always`)
- **Conteneurs Docker** : Lancés automatiquement par Spring Boot

Aucun fichier `.env`, aucune variable d'environnement, aucune configuration manuelle requise.

## Tests

```bash
./mvnw test
```

Les tests utilisent Testcontainers pour créer des instances isolées de PostgreSQL et Kafka.
