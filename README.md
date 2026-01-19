Architecture mise à jour

                      ┌─────────────────┐
                      │     Nginx       │
                      │   (port 80)     │
                      └────────┬────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
      ┌─────────────────┐           ┌─────────────────┐
      │    Frontend     │           │    Backend      │
      │  (port 3000)    │           │  (port 8080)    │
      └─────────────────┘           └─────────────────┘

Accès
┌──────────────────────────────────┬─────────────┐
│               URL                │   Service   │
├──────────────────────────────────┼─────────────┤
│ http://localhost/                │ Frontend    │
├──────────────────────────────────┼─────────────┤
│ http://localhost/api/chambres    │ API Backend │
├──────────────────────────────────┼─────────────┤
│ http://localhost/swagger-ui.html │ Swagger UI  │
├──────────────────────────────────┼─────────────┤
│ http://localhost:8080            │ Kafka UI    │
└──────────────────────────────────┴─────────────┘


Nouveaux fichiers créés
- domain/ports/EventPublisherPort.java - interface pour la publication d'events
- domain/ports/PasswordEncoderPort.java - interface pour l'encodage mot de passe
- frameworks/security/PasswordEncoderAdapter.java - implémente PasswordEncoderPort
- frameworks/config/UseCaseConfig.java - instancie les UseCases comme beans Spring

UseCases nettoyés (Pure Java maintenant)

- Retiré @Service des 4 UseCases
- Remplacé imports frameworks.kafka.EventPublisher → domain.ports.EventPublisherPort
- Remplacé imports spring.security.crypto.password.PasswordEncoder → domain.ports.PasswordEncoderPort

Controllers améliorés

- Retiré / au début des @RequestMapping et endpoints
- Ajouté @ApiResponses avec codes 200, 400, 404, 500
- Ajouté descriptions détaillées sur @Operation

Architecture respectée

domain/     → Pure Java (entities, repositories, ports, events, exceptions)
usecase/    → Pure Java (business logic, dépend uniquement du domain)
adapters/   → Spring autorisé (persistence, web)
frameworks/ → Spring config (UseCaseConfig, SecurityConfig, Kafka)

