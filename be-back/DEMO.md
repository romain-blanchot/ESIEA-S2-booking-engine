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
│  │                         │  │  - Mappers                  │    │
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
│   ├── ports/                  # Ports (EventPublisher, PasswordEncoder)
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
    ├── config/                 # Configuration UseCases
    ├── security/               # Spring Security
    └── kafka/                  # Configuration Kafka
```

---

## 3. Architecture Docker

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

---

## 4. Plan de Présentation

### Étape 1 : Lancement (2 min)

```bash
docker compose up --build
```

**Points à montrer :**
- Tous les services démarrent (Nginx, Frontend, Backend, PostgreSQL, Kafka)
- Application accessible sur http://localhost
- Swagger accessible sur http://localhost/swagger-ui.html

---

### Étape 2 : Swagger - API REST (5 min)

**URL** : http://localhost/swagger-ui.html

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

### Étape 3 : Frontend Next.js (3 min)

**URL** : http://localhost

**Points à montrer :**
- Interface utilisateur moderne (React/Next.js)
- Navigation entre les pages
- Appels API vers le backend via Nginx

---

### Étape 4 : Kafka Events (2 min)

**Montrer dans les logs :**

```bash
docker compose logs -f backend
```

```
Event publié sur booking.chambres : {"chambreId":1,"numero":"101",...}
Reçu event chambre: {"chambreId":1,"numero":"101",...}
```

**Accéder à Kafka UI :** http://localhost:8080

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

## 5. Commandes utiles

### Démarrage

```bash
# Lancer tous les services
docker compose up --build

# Lancer en arrière-plan
docker compose up -d --build
```

### Reset complet

```bash
# Arrêter et supprimer les volumes (DB + Kafka)
docker compose down -v

# Relancer
docker compose up --build
```

### Logs

```bash
# Logs Backend
docker compose logs -f backend

# Logs Nginx
docker compose logs -f nginx

# Logs Kafka
docker compose logs -f kafka
```

---

## 6. Données de test suggérées

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

## 7. URLs de la démo

| Service | URL |
|---------|-----|
| Application | http://localhost |
| Swagger UI | http://localhost/swagger-ui.html |
| OpenAPI JSON | http://localhost/v3/api-docs |
| API Saisons | http://localhost/api/saisons |
| API Chambres | http://localhost/api/chambres |
| API Prix | http://localhost/api/prix/calculer |
| Kafka UI | http://localhost:8080 |

**Credentials par défaut :** `user` / `password`
