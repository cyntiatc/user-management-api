# User Management API — Projet KFOKAM48

API REST sécurisée de gestion des utilisateurs construite avec **Spring Boot 3**, **Spring Security 6 (JWT + BCrypt)** et **PostgreSQL**.

---

## Description

Ce projet expose une API REST permettant de :
- **Créer** un compte utilisateur (inscription)
- **Authentifier** un utilisateur et obtenir un token JWT
- **Lister, consulter, modifier et supprimer** des utilisateurs (opérations protégées par JWT)

---

## Stack Technique

| Technologie         | Version     |
|---------------------|-------------|
| Java                | 21          |
| Spring Boot         | 3.5.14      |
| Spring Security     | 6.x         |
| Spring Data JPA     | 3.x         |
| PostgreSQL          | 15+         |
| JJWT                | 0.12.6      |
| SpringDoc OpenAPI   | 2.8.16      |
| Lombok              | dernière stable |

---

## Architecture

Le projet suit une architecture en couches strictes :

```
Controller  →  Service  →  Repository  →  Base de données
                ↕
             Mapper  (Entity ↔ DTO)
                ↕
           Exception  (gestion centralisée des erreurs)
```

---

## Structure du Projet

```
src/main/java/com/cyntia/user_management_api/
├── UserManagementApiApplication.java
├── config/
│   ├── JwtUtil.java                   # Génération et validation des tokens JWT
│   ├── JwtAuthenticationFilter.java   # Filtre HTTP — intercepte et valide le token
│   ├── SecurityConfig.java            # Configuration Spring Security (routes, CORS, session)
│   └── SwaggerConfig.java             # Configuration OpenAPI / Swagger UI
├── controller/
│   ├── AuthController.java            # POST /auth/register, POST /auth/login
│   └── UserController.java            # GET|PUT|DELETE /users/**
├── dto/
│   ├── UserRequest.java               # Corps de requête création/mise à jour
│   ├── UserResponse.java              # Corps de réponse utilisateur (sans mot de passe)
│   ├── LoginRequest.java              # Corps de requête connexion
│   └── AuthResponse.java             # Réponse avec token JWT
├── exception/
│   ├── ApiError.java                  # Structure JSON standardisée des erreurs
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice — handler central
│   ├── ResourceNotFoundException.java # 404 Not Found
│   ├── ConflictException.java         # 409 Conflict
│   ├── BadRequestException.java       # 400 Bad Request
│   ├── UnauthorizedException.java     # 401 Unauthorized
│   └── ValidationException.java      # 422 Unprocessable Entity
├── mapper/
│   └── UserMapper.java                # Conversion Entity ↔ DTO (méthodes statiques)
├── model/
│   ├── User.java                      # Entité JPA (@Entity)
│   └── Role.java                      # Enum (ROLE_USER, ROLE_ADMIN)
├── repository/
│   └── UserRepository.java            # JpaRepository<User, Long>
└── service/
    ├── AuthService.java               # Interface
    ├── UserService.java               # Interface
    └── impl/
        ├── AuthServiceImpl.java
        ├── UserServiceImpl.java
        └── UserDetailsServiceImpl.java
```

---

## Prérequis

- **Java 21** installé et dans le `PATH`
- **Maven 3.9+** (ou utiliser le wrapper `./mvnw` / `mvnw.cmd`)
- **PostgreSQL 15+** en cours d'exécution

---

## Configuration

### 1. Créer la base de données

```sql
CREATE DATABASE user_management_db;
```

### 2. Définir les variables d'environnement

Copiez le fichier exemple et renseignez vos valeurs :

```bash
cp .env.example .env
```

Contenu du `.env` :

```
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-super-secret-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
```

> **Important** — Le fichier `.env` ne doit **jamais** être commité. Il est déjà dans `.gitignore`.

#### Définir les variables sous Linux / macOS

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=your-super-secret-key-at-least-32-characters-long
```

#### Définir les variables sous Windows (PowerShell)

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="your-super-secret-key-at-least-32-characters-long"
```

#### Définir les variables sous Windows (Invite de commandes)

```cmd
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set JWT_SECRET=your-super-secret-key-at-least-32-characters-long
```

---

## Lancement

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

L'application démarre sur : `http://localhost:8080`

Swagger UI : `http://localhost:8080/api/v1/swagger-ui.html`

---

## Variables d'Environnement

| Variable         | Obligatoire | Description                                    | Exemple                         |
|------------------|-------------|------------------------------------------------|---------------------------------|
| `DB_USERNAME`    | Oui         | Nom d'utilisateur PostgreSQL                   | `postgres`                      |
| `DB_PASSWORD`    | Oui         | Mot de passe PostgreSQL                        | `postgres`                      |
| `JWT_SECRET`     | Oui         | Clé secrète HMAC-SHA256 (min. 32 caractères)  | `your-super-secret-key-...`     |
| `JWT_EXPIRATION` | Non         | Durée de validité du token en ms (défaut: 24h) | `86400000`                      |

---

## Endpoints de l'API

Toutes les routes sont préfixées par `/api/v1`.

### Authentification (public)

| Méthode | Endpoint            | Description                          | Auth requise |
|---------|---------------------|--------------------------------------|--------------|
| `POST`  | `/auth/register`    | Inscription d'un nouvel utilisateur  | Non          |
| `POST`  | `/auth/login`       | Connexion et obtention du token JWT  | Non          |

### Utilisateurs (JWT requis)

| Méthode  | Endpoint       | Description                        | Auth requise |
|----------|----------------|------------------------------------|--------------|
| `GET`    | `/users`       | Lister tous les utilisateurs       | JWT          |
| `GET`    | `/users/{id}`  | Récupérer un utilisateur par son ID | JWT         |
| `PUT`    | `/users/{id}`  | Mettre à jour un utilisateur       | JWT          |
| `DELETE` | `/users/{id}`  | Supprimer un utilisateur           | JWT          |

### Documentation

| Méthode | Endpoint            | Description             |
|---------|---------------------|-------------------------|
| `GET`   | `/swagger-ui.html`  | Interface Swagger UI    |
| `GET`   | `/api-docs`         | Spécification OpenAPI   |

---

## Authentification JWT

### Flux complet

```
1. POST /auth/register  →  créer un compte
2. POST /auth/login     →  obtenir un token JWT
3. GET  /users          →  envoyer le token dans le header Authorization
```

### Utiliser le token

Ajoutez ce header à chaque requête protégée :

```
Authorization: Bearer <votre_token_jwt>
```

Les tokens expirent après **24h** (configurable via `JWT_EXPIRATION`).

---

## Exemples de Requêtes / Réponses

### Inscription

**Requête**
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "nom": "Alice Martin",
  "email": "alice@example.com",
  "password": "motdepasse123",
  "roles": ["ROLE_USER"]
}
```

**Réponse** `201 Created`
```json
{
  "id": 1,
  "nom": "Alice Martin",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"]
}
```

### Connexion

**Requête**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "motdepasse123"
}
```

**Réponse** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "alice@example.com",
  "nom": "Alice Martin"
}
```

### Lister les utilisateurs

**Requête**
```http
GET /api/v1/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Réponse** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Alice Martin",
    "email": "alice@example.com",
    "roles": ["ROLE_USER"]
  }
]
```

### Erreur — format standardisé

```json
{
  "timestamp": "2026-06-10T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur introuvable avec l'id : 99"
}
```

### Erreur de validation

```json
{
  "timestamp": "2026-06-10T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erreur de validation des données",
  "details": {
    "email": "Format d'email invalide",
    "password": "Le mot de passe doit contenir au moins 6 caractères"
  }
}
```

---

## Via curl

```bash
# 1. Inscription
curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nom":"Alice","email":"alice@example.com","password":"secret123","roles":["ROLE_USER"]}'

# 2. Connexion — récupération du token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret123"}' | jq -r '.token')

# 3. Lister les utilisateurs avec le token
curl -s http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

## Sécurité

- Les mots de passe sont chiffrés avec **BCrypt** avant stockage (jamais en clair).
- Les tokens JWT sont signés avec **HMAC-SHA256** (clé min. 256 bits).
- Les sessions sont **stateless** — aucun état côté serveur.
- Le filtre `JwtAuthenticationFilter` valide le token à chaque requête protégée.
- La clé secrète JWT est injectée **uniquement via variable d'environnement** (`JWT_SECRET`).