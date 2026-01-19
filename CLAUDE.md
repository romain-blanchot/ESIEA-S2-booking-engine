# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hotel booking engine monorepo with a Spring Boot backend (Clean Architecture) and Next.js static frontend.

## Commands

### Development (Full Stack)
```bash
docker compose -f compose.yaml up --build     # Start all services
docker compose -f compose.yaml down -v        # Stop and reset database
```

### Backend Only (be-back/)
```bash
cd be-back
./mvnw spring-boot:run                        # Auto-starts Postgres & Kafka via Docker
./mvnw test                                   # Run all tests
./mvnw test -Dtest=ClassName                  # Run single test class
./mvnw test -Dtest=ClassName#methodName       # Run single test method
./mvnw compile                                # Compile only
./mvnw package -DskipTests                    # Build JAR
```

### Frontend Only (be-front/)
```bash
cd be-front
pnpm install                                  # Install dependencies
pnpm dev                                      # Dev server
pnpm build                                    # Static export to out/
```

## Architecture

### Backend - Clean Architecture (4 Layers)

```
be-back/src/main/java/bookingengine/
├── domain/              # Pure Java - NO framework dependencies
│   ├── entities/        # Saison, Chambre, Utilisateur
│   ├── repositories/    # Repository interfaces (contracts)
│   ├── ports/           # EventPublisherPort, PasswordEncoderPort
│   ├── events/          # Domain events (ChambreCreatedEvent, etc.)
│   └── exceptions/      # EntityNotFoundException
│
├── usecase/             # Pure Java - Business logic only
│   ├── saison/          # SaisonUseCase
│   ├── chambre/         # ChambreUseCase
│   ├── prix/            # CalculPrixUseCase (+ ResultatCalculPrix, DetailJour)
│   └── auth/            # AuthUseCase
│
├── adapters/            # Framework allowed here
│   ├── persistence/     # JPA entities, mappers, repository implementations
│   └── web/             # REST controllers, DTOs
│
└── frameworks/          # Spring configuration
    ├── config/          # UseCaseConfig (instantiates UseCases as beans)
    ├── security/        # SecurityConfig, PasswordEncoderAdapter
    └── kafka/           # KafkaConfig, EventPublisher, EventListener
```

**Dependency Rule**:
- `domain/` and `usecase/` = Pure Java only, NO Spring annotations (@Service, @Component, etc.)
- `adapters/` and `frameworks/` = Spring/JPA/Kafka allowed
- Dependencies point inward: frameworks → adapters → usecase → domain

### Ports Pattern (Dependency Inversion)
- `domain/ports/EventPublisherPort` → implemented by `frameworks/kafka/EventPublisher`
- `domain/ports/PasswordEncoderPort` → implemented by `frameworks/security/PasswordEncoderAdapter`
- UseCases receive ports via constructor injection (configured in `UseCaseConfig`)

### Key Domain Entities
- `Saison`: Pricing season with coefficient (e.g., "Haute Saison" = 1.5x)
- `Chambre`: Hotel room with type, base price, capacity
- `Utilisateur`: User for authentication

### Event-Driven Pattern
UseCase → `EventPublisherPort.publish()` → Kafka Topics → `EventListener` consumes

**Kafka Topics**: `booking.chambres`, `booking.saisons`, `booking.prix`

### Frontend - Next.js Static Export
- Output mode: `export` (generates static HTML in `out/`)
- Served via Nginx in Docker
- App Router structure in `app/`

## Services & Ports

| Service | Port | Description |
|---------|------|-------------|
| nginx | 80 | Reverse proxy (entry point) |
| frontend | 3000 | Next.js (internal) |
| backend | 8080 | Spring Boot API (internal) |
| kafka-ui | 8080 | Kafka monitoring |
| kafka | 9092 | Message broker |
| postgres | 5432 | Database |

### Nginx Reverse Proxy
- All traffic goes through `http://localhost:80`
- `/api/*` → Backend (Spring Boot)
- `/swagger-ui/*` → Backend (Swagger UI)
- `/*` → Frontend (Next.js)

## API Endpoints

- `GET/POST api/saisons` - Season CRUD
- `GET/POST api/chambres` - Room CRUD
- `POST api/prix/calculer` - Price calculation with day-by-day breakdown
- `POST api/auth/inscription` - User registration
- `GET swagger-ui.html` - API documentation

All endpoints have `@ApiResponses` with 200, 400, 404, 500 codes and descriptions.

## Key Implementation Details

### Price Calculation (`CalculPrixUseCase`)
Iterates each night, applies seasonal coefficient per date, returns total + detailed breakdown.

### Mapper Pattern (`adapters/persistence/mappers/`)
Converts domain ↔ JPA entities. Filters null/zero IDs to avoid auto-increment conflicts.

### Database
PostgreSQL with Hibernate `ddl-auto=update`. Schema auto-generated from JPA entities.

## Tech Stack

**Backend**: Java 21, Spring Boot 4.0.1, Spring Data JPA, Spring Kafka, Springdoc OpenAPI
**Frontend**: Next.js 16, React 19, TypeScript, Tailwind CSS
**Infrastructure**: PostgreSQL, Kafka (Confluent 7.5.0), Nginx (reverse proxy), Docker Compose
