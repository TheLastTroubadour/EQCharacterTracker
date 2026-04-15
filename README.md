# EQCharacterTracker

A Spring Boot REST API for tracking EverQuest characters, gear, and quest progress across private servers.

## Tech Stack

- **Java 17** / **Spring Boot 3.3.2**
- **Spring Security** + **JWT** (jjwt 0.12.3)
- **Spring Data JPA** + **MySQL**
- **Flyway** for database migrations
- **JSoup** for web scraping live server data
- **Spring Retry** for resilient HTTP calls
- **Lombok** + **ModelMapper**

## Prerequisites

- Java 17+
- MySQL database
- Maven

## Configuration

Set the following in `src/main/resources/application.properties` (or via environment variables):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/eq_tracker
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=validate
```

## Building & Running

```bash
mvn clean install
mvn spring-boot:run
```

The API starts on `http://localhost:8080` by default.

## Authentication

All endpoints except `/auth/**` require a Bearer token.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive a JWT |

**Register / Login request body:**
```json
{ "username": "string", "password": "string" }
```

**Response:**
```json
{ "token": "<jwt>" }
```

Include the token in subsequent requests:
```
Authorization: Bearer <jwt>
```

## API Endpoints

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/user` | Get all users |
| GET | `/user/{userId}` | Get user by ID |

### Servers

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/server` | Create a server |
| GET | `/server` | Get all servers (optional `?type=LIVE\|PRIVATE`) |

### Characters

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/character` | Create a character |
| POST | `/character/batch` | Create multiple characters |
| GET | `/character` | Get all characters |
| GET | `/character/{characterId}` | Get character by ID |
| GET | `/character/refresh` | Refresh character data from server (optional `?server=<name>`) |
| POST | `/character/server/{server}/guild/{guild}` | Bulk-create characters by server and guild name |
| GET | `/character/{characterId}/server/{serverName}/equipped-armor` | Get a character's equipped armor |
| GET | `/character/refresh/inventory` | Refresh inventory for the authenticated user's characters |

### Import

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/import` | Bulk import characters with equipped items and augmentations |

### Quest Progress (Generic)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/quest-progress?characterId=&questTemplateId=` | Start tracking a quest for a character |
| GET | `/quest-progress/character/{characterId}` | Get all quest progress for a character |
| PATCH | `/quest-progress/{progressId}/requirement/{requirementProgressId}` | Toggle a requirement as complete/incomplete |

### THF Server — Tier 5 Quests

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tier-5/xegony-keys` | Get all Tier 5 Xegony Key quests |
| GET | `/tier-5/poa-augments` | Get all Tier 5 PoA Augment quests |

### THF Server — Epic 1.5 Quests

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/epic-1-5` | Get all Epic 1.5 quests |
| GET | `/epic-1-5/{userId}` | Get Epic 1.5 quests for a specific user |

## Project Structure

```
src/main/java/com/eq/charactertracker/
├── config/          # Spring Security, JWT filter, ModelMapper, CORS
├── constants/       # Enums: CharacterClass, EquipSlotEnum, ServerType, ItemType, quest items
├── controller/      # REST controllers
├── entity/          # JPA entities
│   └── quest/       # Quest template and progress entities
├── exception/       # Global exception handler and custom exceptions
├── factory/         # CharacterInformation factory (strategy pattern)
├── model/           # Request/response models
│   ├── auth/        # Login/register/auth response
│   ├── imports/     # Bulk import models
│   └── quest/       # Quest progress response models
├── repo/            # Spring Data JPA repositories
├── service/         # Business logic
└── util/            # Utility classes
```

## Features

- **JWT-secured REST API** — stateless authentication with per-user character isolation
- **Multi-server support** — track characters across LIVE and PRIVATE EverQuest servers
- **Web scraping** — pulls live character and guild data from server websites via JSoup, with Spring Retry for transient failures
- **Equipment tracking** — per-slot armor and augmentation tracking
- **Bulk import** — import characters with full equipped-item and augmentation data in one request
- **Quest progress system** — generic template-based quest tracking with sub-quests and per-requirement toggle
- **THF-specific quests** — dedicated tracking for Tier 5 Xegony Keys, PoA Augments, and Epic 1.5 quests
- **Flyway migrations** — versioned schema management
