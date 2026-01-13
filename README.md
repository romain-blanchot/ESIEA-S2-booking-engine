# Booking Engine

Booking Engine Spring Boot (BLANCHOT, ATTOY, ALLAM)

## Prérequis

- Java 21
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
| Application | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

## Stack technique

- **Framework** : Spring Boot 4.0.1
- **Base de données** : PostgreSQL
- **Messaging** : Apache Kafka
- **Documentation API** : Springdoc OpenAPI (Swagger)
- **Architecture** : Spring Modulith

## Structure du projet

```
src/
├── main/
│   ├── java/bookingengine/
│   │   └── BookingEngineApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/bookingengine/
        └── TestcontainersConfiguration.java
```

## Docker Compose

Les services sont définis dans `compose.yaml` :

- **postgres** : Base de données PostgreSQL (port 5432)
- **kafka** : Apache Kafka (port 9092)

Pour lancer manuellement les conteneurs :

```bash
docker compose up -d
```

Pour arrêter :

```bash
docker compose down
```

## Tests

```bash
./mvnw test
```

Les tests utilisent Testcontainers pour créer des instances isolées de PostgreSQL et Kafka.
