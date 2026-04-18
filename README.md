# Opus

A **Spring Boot REST API** backend built with Java 17, Spring Security, JWT authentication, and PostgreSQL. Opus provides user authentication and a **Zone** management system where users can create and manage collaborative workspaces.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Database Design](#database-design)
- [API Reference](#api-reference)
- [Security Design](#security-design)
- [Error Handling](#error-handling)
- [Local Setup](#local-setup)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.1.4 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL (Aiven Cloud) |
| Validation | Jakarta Validation + Hibernate Validator |
| JSON Support | Vladmihalcea Hibernate Types (JSONB) |
| Build Tool | Gradle |
| Boilerplate | Lombok |

---

## Project Structure

```
src/main/java/com/opus/
├── OpusMain.java                  # Application entry point
├── controller/
│   ├── UserController.java        # /api/users endpoints
│   └── ZoneController.java        # /api/zones endpoints
├── dto/                           # Request / Response objects
│   ├── SignUpRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── UserDetailsResponse.java
│   ├── CreateZoneRequest.java
│   ├── UpdateZoneRequest.java
│   └── ZoneResponse.java
├── model/                         # JPA Entities
│   ├── User.java
│   ├── Zone.java
│   └── UserZoneMap.java
├── repository/                    # Spring Data JPA Repositories
│   ├── UserRepository.java
│   ├── ZoneRepository.java
│   └── UserZoneMapRepository.java
├── service/                       # Business logic
│   ├── UserService.java
│   └── ZoneService.java
├── security/                      # Auth & filter chain
│   ├── SecurityConfig.java
│   └── JwtAuthenticationFilter.java
├── util/
│   ├── JWTUtil.java               # Token generation & validation
│   └── PasswordUtil.java          # SHA-256 + salt hashing
└── exception/                     # Custom exceptions & global handler
    ├── BaseException.java
    ├── GlobalExceptionHandler.java
    ├── ErrorConstants.java
    └── ...
```

---

## Architecture

```
Client (Bruno / Frontend)
        │
        │  HTTP Request (with Bearer token)
        ▼
┌─────────────────────────────────────────┐
│           Spring Boot App               │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │   JwtAuthenticationFilter        │   │  ← Runs on every request
│  │   - Extracts & validates token   │   │  ← Injects User into SecurityContext
│  └──────────────┬───────────────────┘   │
│                 │                        │
│  ┌──────────────▼───────────────────┐   │
│  │        Controller Layer           │   │  ← Routes HTTP → Service
│  │  UserController / ZoneController  │   │
│  └──────────────┬───────────────────┘   │
│                 │                        │
│  ┌──────────────▼───────────────────┐   │
│  │         Service Layer             │   │  ← Business logic, transactions
│  │   UserService / ZoneService       │   │
│  └──────────────┬───────────────────┘   │
│                 │                        │
│  ┌──────────────▼───────────────────┐   │
│  │       Repository Layer            │   │  ← Spring Data JPA
│  │  UserRepository / ZoneRepository  │   │
│  │  UserZoneMapRepository            │   │
│  └──────────────┬───────────────────┘   │
└─────────────────┼───────────────────────┘
                  │
                  ▼
        PostgreSQL (Aiven Cloud)
```

### Key Architectural Decisions

- **Stateless** — No sessions. Every request is authenticated via JWT.
- **Role-based Zone Access** — A `UserZoneMap` join table controls who can access a zone and at what role (OWNER / MEMBER).
- **Owner-only mutations** — Only the zone OWNER can update or delete a zone.
- **Hash-based Zone Lookup** — Every zone gets a unique UUID-based `zoneHash` for public/shareable lookups without exposing the internal `zoneId`.
- **DTO separation** — Models are never returned directly; all responses go through DTO mappers.

---

## Database Design

### Entity Relationship Diagram

```
┌──────────────────────────┐          ┌──────────────────────────────┐
│        user_table         │          │        user_zone_map_table    │
│──────────────────────────│          │──────────────────────────────│
│ PK  user_id   BIGSERIAL  │◄────────┤ FK  user_id   BIGINT NOT NULL │
│     name      VARCHAR(100)│          │ FK  zone_id   BIGINT NOT NULL │
│     username  VARCHAR(50) │          │ PK  uzmap_id  BIGSERIAL       │
│     email     VARCHAR(100)│          │     role      VARCHAR(20)      │
│     password_hash TEXT    │          │               (OWNER|MEMBER)  │
│     password_salt TEXT    │          │     joined_at TIMESTAMP       │
│     created_at TIMESTAMP  │          │     metadata  JSONB           │
│     updated_at TIMESTAMP  │          │ UQ (user_id, zone_id)        │
└──────────────────────────┘          └──────────────┬───────────────┘
                                                      │
                                                      │ FK
                                                      ▼
                                       ┌──────────────────────────────┐
                                       │         zone_table            │
                                       │──────────────────────────────│
                                       │ PK  zone_id   BIGSERIAL       │
                                       │     zone_name VARCHAR(100)    │
                                       │     zone_type VARCHAR(10)     │
                                       │               (DESK|BOARD)   │
                                       │     zone_hash VARCHAR(32) UQ  │
                                       │     user_id   BIGINT          │  ← creator ref
                                       │     metadata  JSONB           │
                                       │     created_at TIMESTAMP      │
                                       │     updated_at TIMESTAMP      │
                                       └──────────────────────────────┘
```

### Tables

#### `user_table`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `user_id` | BIGSERIAL | PK | Auto-incremented user ID |
| `name` | VARCHAR(100) | NOT NULL | Display name |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | Login handle |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | Login email |
| `password_hash` | TEXT | NOT NULL | SHA-256 hashed password |
| `password_salt` | TEXT | NOT NULL | Random Base64 salt |
| `created_at` | TIMESTAMP | NOT NULL | Set on insert |
| `updated_at` | TIMESTAMP | | Updated on every change |

#### `zone_table`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `zone_id` | BIGSERIAL | PK | Auto-incremented zone ID |
| `zone_name` | VARCHAR(100) | NOT NULL | Human-readable name |
| `zone_type` | VARCHAR(10) | NOT NULL | Enum: `DESK` or `BOARD` |
| `zone_hash` | VARCHAR(32) | UNIQUE | UUID-based public identifier |
| `user_id` | BIGINT | NOT NULL | Creator's user ID |
| `metadata` | JSONB | | Extensible key-value store |
| `created_at` | TIMESTAMP | NOT NULL | Set on insert |
| `updated_at` | TIMESTAMP | | Updated on every change |

#### `user_zone_map_table`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `uzmap_id` | BIGSERIAL | PK | Auto-incremented mapping ID |
| `user_id` | BIGINT | NOT NULL, FK | Reference to `user_table` |
| `zone_id` | BIGINT | NOT NULL, FK | Reference to `zone_table` |
| `role` | VARCHAR(20) | NOT NULL | Enum: `OWNER` or `MEMBER` |
| `joined_at` | TIMESTAMP | NOT NULL | Set on insert |
| `metadata` | JSONB | | Extensible key-value store |
| | | UQ(`user_id`, `zone_id`) | A user can only be in a zone once |

---

## API Reference

> All endpoints except `/signup` and `/login` require `Authorization: Bearer <token>` header.

### User Endpoints — `/api/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/users/signup` | ❌ Public | Register a new user |
| `POST` | `/api/users/login` | ❌ Public | Login and receive JWT token |
| `GET` | `/api/users/me` | ✅ Required | Get logged-in user's profile |

#### POST `/api/users/signup`
```json
// Request
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "secret123"
}

// Response: 200 OK
"Signup Successful!"
```

#### POST `/api/users/login`
```json
// Request
{
  "usernameOrEmail": "johndoe",
  "password": "secret123"
}

// Response: 200 OK
{
  "username": "johndoe",
  "token": "<JWT>"
}
```

#### GET `/api/users/me`
```json
// Response: 200 OK
{
  "username": "johndoe",
  "email": "john@example.com",
  "name": "John Doe"
}
```

---

### Zone Endpoints — `/api/zones`

| Method | Endpoint | Auth | Role Required | Description |
|---|---|---|---|---|
| `POST` | `/api/zones` | ✅ | Any | Create a new zone |
| `GET` | `/api/zones` | ✅ | Any | Get all zones for current user |
| `GET` | `/api/zones/{zoneId}` | ✅ | Member+ | Get zone by ID |
| `GET` | `/api/zones/hash/{zoneHash}` | ✅ | Member+ | Get zone by hash |
| `PUT` | `/api/zones/{zoneId}` | ✅ | OWNER only | Update a zone |
| `DELETE` | `/api/zones/{zoneId}` | ✅ | OWNER only | Delete a zone |

#### POST `/api/zones`
```json
// Request
{
  "zoneName": "My Workspace",
  "zoneType": "DESK"
}

// Response: 200 OK — ZoneResponse
{
  "zoneId": 1,
  "zoneName": "My Workspace",
  "zoneType": "DESK",
  "zoneHash": "a1b2c3d4..."
}
```

#### PUT `/api/zones/{zoneId}`
```json
// Request (all fields optional)
{
  "zoneName": "Updated Name",
  "zoneType": "BOARD"
}
```

#### DELETE `/api/zones/{zoneId}`
```
// Response: 200 OK
"Zone deleted successfully"
```

---

## Security Design

```
Signup/Login  ──────────────────────────────────────────► No auth needed
                                                           Password hashed with
                                                           SHA-256 + random salt

All other requests:
Client ──► [JwtAuthenticationFilter]
              │
              ├─ No/invalid token  ──► 401 Unauthorized (stops here)
              │
              └─ Valid token
                    │
                    ├─ Extract userId from JWT subject claim
                    ├─ Load User from DB
                    └─ Set SecurityContext ──► Controller receives @AuthenticationPrincipal User
```

### Password Hashing
- Algorithm: **SHA-256**
- Salt: **16-byte random salt** generated via `SecureRandom`, stored as Base64
- The salt is unique per user and stored alongside the hash in `user_table`

### JWT
- Library: `jjwt 0.11.5`
- Signing: **HMAC-SHA** with a secret key from `application.yml`
- Claims: `sub` = `userId`, `iat` = issued at, `exp` = expiry
- Expiry: Configurable via `jwt.expiration-ms` (default: 86400000ms = 24h)

---

## Error Handling

All exceptions are caught by `GlobalExceptionHandler` and return a consistent JSON error response.

| Exception | HTTP Status | Scenario |
|---|---|---|
| `UserAlreadyExistsException` | 409 Conflict | Username or email already taken |
| `UserNotFoundException` | 404 Not Found | User doesn't exist |
| `InvalidCredentialsException` | 401 Unauthorized | Wrong password |
| `ZoneNotFoundException` | 404 Not Found | Zone ID/hash doesn't exist |
| `ZoneAccessDeniedException` | 403 Forbidden | User not a member, or not OWNER |
| `InvalidZoneDataException` | 400 Bad Request | Invalid zone type string |
| Validation errors | 400 Bad Request | `@Valid` constraint violations |

---

## Local Setup

### Prerequisites
- Java 17+
- Gradle
- A running PostgreSQL instance (or an Aiven account)
- Bruno (for API testing)

### Steps

**1. Clone the repo**
```bash
git clone https://github.com/demi2k-sudo/Opus.git
cd Opus
```

**2. Create your `application.yml`** from the template
```bash
cp src/main/resources/application.yml.template src/main/resources/application.yml
```

**3. Fill in your config**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://<your-host>:<port>/defaultdb?sslmode=require
    username: <your-username>
    password: ${DB_PASSWORD}        # set as env var

jwt:
  secret: ${JWT_SECRET}             # set as env var, must be 32+ chars
  expiration-ms: 86400000
```

**4. Set environment variables**
```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_long_random_secret_key
```

**5. Run the app**
```bash
./gradlew bootRun
```
Server starts on `http://localhost:8048`

**6. Test with Bruno**

Open Bruno → `Open Collection` → select the `bruno/` folder → choose `Local` environment → run **Sign Up** then **Login**.

