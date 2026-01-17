# Guide de Démonstration - Booking Engine

## 1. Ce que fait Kafka dans ce projet

**Kafka** est un système de messaging distribué. Dans notre projet, il sert à publier des événements métier :

```
┌─────────────────┐     Event      ┌─────────────────┐     Message     ┌─────────────────┐
│   Use Case      │ ───────────>   │  EventPublisher │ ───────────>   │  Kafka Broker   │
│ (creerChambre)  │                │                 │                │  (Topic)        │
└─────────────────┘                └─────────────────┘                └────────┬────────┘
                                                                               │
                                                                               v
                                                                    ┌─────────────────┐
                                                                    │  EventListener  │
                                                                    │  (consomme)     │
                                                                    └─────────────────┘
```

### 3 Topics Kafka

| Topic | Événement | Déclencheur |
|-------|-----------|-------------|
| `booking.chambres` | ChambreCreatedEvent | Création d'une chambre |
| `booking.saisons` | SaisonCreatedEvent | Création d'une saison |
| `booking.prix` | PrixCalculatedEvent | Calcul d'un prix |

### Utilité

- **Découplage** : Les services sont indépendants
- **Traçabilité** : Historique de tous les événements
- **Extensibilité** : Possibilité d'ajouter des consommateurs (notifications, analytics, etc.)

---

## 2. Architecture Clean Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                         FRAMEWORKS                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │   Security   │  │    Kafka     │  │  Spring Boot │            │
│  └──────────────┘  └──────────────┘  └──────────────┘            │
├──────────────────────────────────────────────────────────────────┤
│                          ADAPTERS                                 │
│  ┌─────────────────────────┐  ┌─────────────────────────────┐    │
│  │      WEB (REST API)     │  │    PERSISTENCE (JPA)        │    │
│  │  - Controllers          │  │  - JPA Entities             │    │
│  │  - DTOs                 │  │  - JPA Repositories         │    │
│  │  - Thymeleaf Views      │  │  - Mappers                  │    │
│  └─────────────────────────┘  └─────────────────────────────┘    │
├──────────────────────────────────────────────────────────────────┤
│                          USE CASES                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │  Saison  │  │ Chambre  │  │   Prix   │  │   Auth   │          │
│  │ UseCase  │  │ UseCase  │  │ UseCase  │  │ UseCase  │          │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘          │
├──────────────────────────────────────────────────────────────────┤
│                           DOMAIN                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │   Entities   │  │ Repositories │  │    Events    │            │
│  │ Saison,      │  │ (interfaces) │  │ ChambreCreated│           │
│  │ Chambre,     │  │              │  │ SaisonCreated │           │
│  │ Utilisateur  │  │              │  │ PrixCalculated│           │
│  └──────────────┘  └──────────────┘  └──────────────┘            │
└──────────────────────────────────────────────────────────────────┘
```

### Structure des dossiers

```
src/main/java/bookingengine/
├── domain/                     # Couche Domaine (Entités métier)
│   ├── entities/               # Saison, Chambre, Utilisateur
│   ├── repositories/           # Interfaces des repositories
│   ├── events/                 # Événements métier
│   └── exceptions/             # Exceptions métier
│
├── usecase/                    # Couche Use Cases (Logique métier)
│   ├── saison/                 # CRUD Saisons
│   ├── chambre/                # CRUD Chambres
│   ├── prix/                   # Calcul prix avec coefficients
│   └── auth/                   # Authentification
│
├── adapters/                   # Couche Adapters
│   ├── persistence/            # JPA (entities, repositories, mappers)
│   └── web/                    # REST Controllers + DTOs
│
└── frameworks/                 # Couche Frameworks
    ├── security/               # Spring Security
    └── kafka/                  # Configuration Kafka
```

---

## 3. Plan de Présentation

### Étape 1 : Lancement (2 min)

```bash
./mvnw spring-boot:run
```

**Points à montrer :**
- Docker démarre automatiquement (PostgreSQL + Kafka)
- Logs de démarrage Spring Boot
- Application accessible sur http://localhost:8080

---

### Étape 2 : Swagger - API REST (5 min)

**URL** : http://localhost:8080/swagger-ui.html

#### 2.1 Créer une saison (POST /api/saisons)

```json
{
  "nom": "Haute Saison",
  "dateDebut": "2024-07-01",
  "dateFin": "2024-08-31",
  "coefficientPrix": 1.5
}
```

#### 2.2 Créer une chambre (POST /api/chambres)

```json
{
  "numero": "101",
  "type": "DOUBLE",
  "prixBase": 80,
  "capacite": 2,
  "description": "Chambre double vue mer",
  "disponible": true
}
```

#### 2.3 Calculer un prix (POST /api/prix/calculer)

```json
{
  "chambreId": 1,
  "dateDebut": "2024-07-15",
  "dateFin": "2024-07-20"
}
```

**Résultat attendu :**
- 5 nuits × 80€ × 1.5 (haute saison) = **600€**
- Détail jour par jour avec coefficient appliqué

---

### Étape 3 : Interface Front Thymeleaf (3 min)

| Page | URL | Fonctionnalités |
|------|-----|-----------------|
| Accueil | http://localhost:8080 | Navigation générale |
| Saisons | http://localhost:8080/saisons | CRUD saisons |
| Chambres | http://localhost:8080/chambres | CRUD chambres |
| Calcul Prix | http://localhost:8080/prix | Simulateur de prix |

**Points à montrer :**
- Navigation entre les pages
- Formulaire de création (Nouvelle Saison / Nouvelle Chambre)
- Liste avec boutons Modifier / Supprimer
- Calcul de prix avec détail jour par jour
- Affichage des coefficients saisonniers

---

### Étape 4 : Kafka Events (2 min)

**Montrer dans les logs de la console :**

```
Event publié sur booking.chambres : {"chambreId":1,"numero":"101",...}
Reçu event chambre: {"chambreId":1,"numero":"101",...}
```

**Expliquer :**
- Chaque création publie un événement
- Le listener consomme et log les événements
- Possibilité d'ajouter d'autres consommateurs

---

### Étape 5 : Architecture (3 min)

**Points clés à expliquer :**

1. **Clean Architecture** : Séparation en couches (Domain → UseCase → Adapters → Frameworks)

2. **SOLID** :
   - **S**ingle Responsibility : Chaque classe a une seule responsabilité
   - **O**pen/Closed : Extensible sans modification (via interfaces)
   - **L**iskov Substitution : Les implémentations sont interchangeables
   - **I**nterface Segregation : Interfaces spécifiques (Repository par entité)
   - **D**ependency Inversion : Le domain ne dépend pas des frameworks

3. **Découplage** : Le domain ne connaît pas JPA, Kafka, ou Spring

---

## 4. Commandes utiles

### Démarrage

```bash
# Cold start (première fois ou après reset)
./mvnw spring-boot:run
```

### Reset complet

```bash
# Arrêter et supprimer les volumes (DB + Kafka)
docker compose -f compose.yaml down -v

# Relancer
docker compose -f compose.yaml up -d

# Ou simplement relancer l'app (Spring Boot relance Docker)
./mvnw spring-boot:run
```

### Logs

```bash
# Logs Kafka
docker compose -f compose.yaml logs -f kafka

# Logs PostgreSQL
docker compose -f compose.yaml logs -f postgres
```

---

## 5. Données de test suggérées

### Saisons

| Nom | Début | Fin | Coefficient |
|-----|-------|-----|-------------|
| Basse Saison | 2024-01-01 | 2024-03-31 | 0.8 |
| Moyenne Saison | 2024-04-01 | 2024-06-30 | 1.0 |
| Haute Saison | 2024-07-01 | 2024-08-31 | 1.5 |
| Moyenne Saison | 2024-09-01 | 2024-12-31 | 1.0 |

### Chambres

| Numéro | Type | Prix Base | Capacité |
|--------|------|-----------|----------|
| 101 | SIMPLE | 50€ | 1 |
| 102 | DOUBLE | 80€ | 2 |
| 201 | SUITE | 150€ | 2 |
| 202 | FAMILIALE | 120€ | 4 |

### Scénario de calcul

- Chambre 102 (DOUBLE, 80€)
- Du 15 juillet au 20 juillet (5 nuits en haute saison)
- Prix = 5 × 80€ × 1.5 = **600€**

---

## 6. URLs de la démo

| Service | URL |
|---------|-----|
| Accueil | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Saisons | http://localhost:8080/saisons |
| Chambres | http://localhost:8080/chambres |
| Calcul Prix | http://localhost:8080/prix |
| Login | http://localhost:8080/login |

**Credentials par défaut :** `user` / `password`
