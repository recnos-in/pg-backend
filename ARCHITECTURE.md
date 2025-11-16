# PG Backend - Architecture Overview

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLIENT APPLICATIONS                          │
│  (Web Frontend, Mobile Apps, Admin Portal)                      │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP/REST
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                   API GATEWAY / CORS CONFIG                     │
│              (CorsConfig, WebConfig)                            │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│              SECURITY LAYER (Spring Security)                   │
├──────────────────────────────────────────────────────────────────┤
│  1. JwtAuthenticationFilter - Validates Bearer tokens           │
│  2. SecurityConfig - Configures filter chain & public routes    │
│  3. JwtService - Token creation & validation (JJWT)            │
│  4. SecurityContextHolder - Extract current user ID             │
└──────────────────────────┬──────────────────────────────────────┘
                           │ Authenticated User
┌──────────────────────────▼──────────────────────────────────────┐
│              REST CONTROLLER LAYER                              │
├──────────────────────────────────────────────────────────────────┤
│  org.recnos.pg.controller.*                                     │
│                                                                   │
│  ├── auth/           - AuthController, UserAuthController      │
│  ├── user/           - UserController, FavoriteController      │
│  ├── pg/             - PgController, PgSearchController        │
│  ├── owner/          - OwnerController, PgManagementController │
│  ├── payment/        - PaymentController, InvoiceController    │
│  ├── admin/          - AdminPgController, ApprovalController   │
│  └── ...                                                        │
│                                                                   │
│  Responsibilities:                                             │
│  - Route mapping                                               │
│  - Request validation (@Valid)                                 │
│  - Parameter extraction                                        │
│  - HTTP status codes                                           │
└──────────────────────────┬──────────────────────────────────────┘
                           │ Service calls
┌──────────────────────────▼──────────────────────────────────────┐
│              SERVICE LAYER (Business Logic)                     │
├──────────────────────────────────────────────────────────────────┤
│  org.recnos.pg.service.*                                        │
│                                                                   │
│  ├── auth/           - AuthService, JwtService                 │
│  ├── user/           - UserService, FavoriteService            │
│  ├── pg/             - PgService (EMPTY - needs impl.)         │
│  ├── owner/          - OwnerService, SubscriptionService       │
│  ├── payment/        - PaymentService, InvoiceService          │
│  ├── admin/          - ApprovalService, AdminService           │
│  └── ...                                                        │
│                                                                   │
│  Responsibilities:                                             │
│  - @Transactional business logic                               │
│  - Domain validations                                          │
│  - Exception handling                                          │
│  - Cross-entity operations                                     │
│  - Call repositories only                                      │
└──────────────────────────┬──────────────────────────────────────┘
                           │ Repository queries
┌──────────────────────────▼──────────────────────────────────────┐
│              MAPPER LAYER (DTO Conversions)                     │
├──────────────────────────────────────────────────────────────────┤
│  org.recnos.pg.mapper.*                                         │
│                                                                   │
│  ├── UserMapper       - Entity → UserProfileResponse           │
│  ├── OwnerMapper      - Entity → OwnerResponse                 │
│  ├── PgMapper         - Entity → PgDetailResponse              │
│  ├── PaymentMapper    - Entity → PaymentResponse               │
│  └── ...                                                        │
│                                                                   │
│  Pattern: @Component beans with toDTO() methods                │
└──────────────────────────┬──────────────────────────────────────┘
                           │ DB operations
┌──────────────────────────▼──────────────────────────────────────┐
│              REPOSITORY LAYER (JPA Data Access)                 │
├──────────────────────────────────────────────────────────────────┤
│  org.recnos.pg.repository.*                                     │
│                                                                   │
│  ├── UserRepository   - extends JpaRepository<User, UUID>      │
│  ├── OwnerRepository  - extends JpaRepository<Owner, UUID>     │
│  ├── PgRepository     - extends JpaRepository<Pg, UUID>        │
│  ├── PaymentRepository                                         │
│  └── ...                                                        │
│                                                                   │
│  Responsibilities:                                             │
│  - CRUD operations via JpaRepository methods                   │
│  - Named query methods (Spring Data derives SQL)               │
│  - Optional<T> return types for null safety                    │
└──────────────────────────┬──────────────────────────────────────┘
                           │ Hibernate ORM
┌──────────────────────────▼──────────────────────────────────────┐
│              ORM LAYER (JPA/Hibernate)                          │
├──────────────────────────────────────────────────────────────────┤
│  - Entity mapping                                              │
│  - Relationship management (@ManyToOne, @OneToMany)            │
│  - Query generation                                            │
│  - Lazy loading / Eager loading                                │
│  - Cascade operations (delete, merge)                          │
└──────────────────────────┬──────────────────────────────────────┘
                           │ SQL Queries
┌──────────────────────────▼──────────────────────────────────────┐
│              DATABASE LAYER (PostgreSQL)                        │
├──────────────────────────────────────────────────────────────────┤
│  Tables (managed by Flyway migrations):                        │
│                                                                   │
│  Core:    users, owners, admins                                │
│  PGs:     pgs, pg_rooms, pg_images, pg_amenities, amenities    │
│  User:    favorites, visits, callbacks, reviews                │
│  Payment: subscriptions, subscription_plans, payments, invoices│
│                                                                   │
│  Extensions: UUID (uuid-ossp), PostGIS (geospatial)            │
│  Migrations: Flyway (V1__init_schema.sql, V2__... , ...)       │
└─────────────────────────────────────────────────────────────────┘
```

---

## Request-Response Flow

```
1. HTTP REQUEST
   └─> Authorization: Bearer {JWT_TOKEN}
       Content-Type: application/json
       Body: { ... request DTO ... }

2. SECURITY FILTER
   └─> JwtAuthenticationFilter validates token
   └─> Extracts user ID and sets SecurityContext

3. CONTROLLER
   └─> Route to appropriate handler method
   └─> @Valid triggers validation on @RequestBody
   └─> Calls service method with validated DTO

4. SERVICE (Transactional)
   └─> Business logic and validations
   └─> Repository queries (READ)
   └─> Throw exceptions if invalid
   └─> Mapper converts entity to response DTO
   └─> Returns DTO

5. GLOBAL EXCEPTION HANDLER
   ├─> Catches custom exceptions
   ├─> Converts to ErrorResponse
   └─> Returns with appropriate HTTP status

6. CONTROLLER RESPONSE
   └─> ResponseEntity with DTO or error response
   └─> Jackson serializes to JSON

7. HTTP RESPONSE
   └─> Content-Type: application/json
       Status-Code: 200, 201, 400, 404, 500, etc.
       Body: { ... response DTO ... }
```

---

## Exception Handling Flow

```
Service throws exception
         │
         ▼
Is it a custom exception?
    /        \
  YES       NO
  │         │
  ▼         ▼
GlobalExceptionHandler    Try-catch in filter
catches exception         or returns 500
  │
  ▼
Create ErrorResponse
  │
  ▼
Convert to HTTP status:
  ├─ ResourceNotFoundException -> 404 NOT_FOUND
  ├─ BadRequestException -> 400 BAD_REQUEST
  ├─ UnauthorizedException -> 401 UNAUTHORIZED
  ├─ ForbiddenException -> 403 FORBIDDEN
  ├─ DuplicateResourceException -> 409 CONFLICT
  ├─ InvalidCredentialsException -> 401 UNAUTHORIZED
  └─ Generic Exception -> 500 INTERNAL_SERVER_ERROR
  │
  ▼
Return ErrorResponse:
{
  "timestamp": "2025-11-16T...",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: ...",
  "path": "/v1/user/123",
  "validationErrors": null
}
```

---

## Module Architecture (Domain-Driven)

### Authentication Module
```
controller/auth/
  ├── UserAuthController     (POST /v1/auth/user/register, login)
  ├── OwnerAuthController    (POST /v1/auth/owner/register, login)
  └── AdminAuthController    (POST /v1/auth/admin/login)
         ↓ uses
service/auth/
  ├── AuthService            (register, login, token refresh)
  └── JwtService             (token generation/validation)
         ↓ uses
repository/
  ├── UserRepository         (findByEmail, existsByEmail)
  └── OwnerRepository        (findByEmail, existsByEmail)
```

### PG Management Module
```
controller/pg/
  └── PgController           (EMPTY - implement here)
       ↓ uses
service/pg/
  └── PgService              (EMPTY - implement here)
       ↓ uses
repository/
  ├── PgRepository           (findByCity, findByStatus)
  ├── OwnerRepository        (findById)
  └── PgImageRepository      (images per PG)
       ↓ uses
mapper/
  └── PgMapper               (toDetailResponse, toListResponse)
       ↓ converts
model/
  ├── entity/
  │   ├── Pg
  │   ├── PgRoom
  │   └── PgImage
  ├── dto/request/
  │   └── PgCreateRequest, PgUpdateRequest
  └── dto/response/
      └── PgDetailResponse, PgListResponse
```

### User Management Module
```
controller/user/
  ├── UserController         (GET /v1/user/me, PUT /v1/user/{id})
  ├── FavoriteController     (POST/DELETE /v1/user/favorites)
  └── VisitController        (POST /v1/user/visit)
       ↓ uses
service/user/
  ├── UserService            (updateUser, changePassword)
  ├── FavoriteService        (addFavorite, removeFavorite)
  └── VisitService           (scheduleVisit, getVisits)
       ↓ uses
repository/
  ├── UserRepository
  ├── FavoriteRepository
  └── VisitRepository
       ↓ uses
mapper/
  └── UserMapper, VisitMapper
       ↓ converts
model/
  ├── entity/
  │   ├── User, Favorite, Visit
  ├── dto/request/
  │   └── UserUpdateRequest, VisitScheduleRequest
  └── dto/response/
      └── UserProfileResponse, VisitResponse
```

---

## Cross-Cutting Concerns Architecture

### Security Architecture
```
Application Request
  ↓
CORS Filter (CorsConfig)
  ↓
JwtAuthenticationFilter
  ├─ Read "Authorization: Bearer {JWT}" header
  ├─ JwtService.extractUserId(token)
  ├─ JwtService.isTokenExpired(token)
  └─ Set UsernamePasswordAuthenticationToken in SecurityContext
  ↓
Spring Security Filter Chain
  ├─ Check URL against permitAll() list:
  │  ├─ /v1/auth/** (public)
  │  ├─ /swagger-ui/** (public)
  │  ├─ /v3/api-docs/** (public)
  │  └─ Others require authentication
  ├─ Require @Transactional for DB access
  └─ BCryptPasswordEncoder for password hashing
  ↓
Controller Method (with @SecurityRequirement annotation)
  ↓
Service Layer can access:
  └─ UUID currentUserId = SecurityContextHolder.getCurrentUserId()
```

### Validation Architecture
```
HTTP Request with @Valid @RequestBody
  ↓
Jakarta Validation Framework
  ├─ Field-level annotations:
  │  ├─ @NotBlank, @NotNull
  │  ├─ @Email, @Pattern
  │  ├─ @Size, @Min, @Max
  │  └─ ... others
  ├─ Triggers validation before method call
  └─ Throws MethodArgumentNotValidException if fails
  ↓
GlobalExceptionHandler.handleValidationException()
  ├─ Extract field errors
  ├─ Build validationErrors Map<String, String>
  └─ Return 400 Bad Request with errors:
      {
        "status": 400,
        "message": "Validation failed",
        "validationErrors": {
          "email": "Invalid email format",
          "name": "Name must be between 2 and 100 characters"
        }
      }
  ↓
Service Layer (additional business validations)
  ├─ Duplicate email/mobile checks
  ├─ Authorization checks
  └─ Throw custom exceptions if needed
```

### Transactional Architecture
```
@Transactional methods in Service:

READ-ONLY QUERIES:
@Transactional(readOnly = true)
public Page<PgListResponse> listPgs(...) {
  // Optimized for read (no change tracking)
  // Rolls back on RuntimeException
}

WRITE OPERATIONS:
@Transactional
public PgDetailResponse createPg(PgCreateRequest request) {
  // Includes change tracking
  // Auto-commits on success
  // Rolls back on RuntimeException
}

NESTED TRANSACTIONS:
@Transactional
public void method1() {
  // Outer transaction
  method2(); // Inner transaction (propagates)
}

@Transactional
private void method2() {
  // Shares outer transaction
}
```

---

## Data Flow Patterns

### List/Search Pattern
```
User Request: GET /v1/pg?city=Mumbai&page=0&size=10
  │
  Controller
    ├─ Parse @RequestParam(city, page, size)
    ├─ Call pgService.listPgs(city, page, size)
    └─ Return ResponseEntity.ok(result)
  │
  Service (readOnly=true)
    ├─ Create Pageable from page/size
    ├─ Call pgRepository.findByCityAndStatus(city, status, pageable)
    ├─ Map each Pg to PgListResponse
    └─ Return Page<PgListResponse>
  │
  Repository
    ├─ Execute Spring Data query
    └─ Return Page of entities from DB
  │
  Database
    └─ SELECT * FROM pgs WHERE city = ? LIMIT ? OFFSET ?
```

### Create Pattern
```
User Request: POST /v1/pg
             Content-Type: application/json
             { "name": "...", "address": "...", ... }
  │
  Controller
    ├─ Validate @Valid @RequestBody PgCreateRequest
    ├─ Call pgService.createPg(request)
    └─ Return ResponseEntity.status(201).body(response)
  │
  Service (readOnly=false, Transactional)
    ├─ Fetch owner from repository
    ├─ Create new Pg entity
    ├─ Set all fields from request
    ├─ Call pgRepository.save(pg)
    ├─ Map Pg to PgDetailResponse
    └─ Return response
  │
  Repository + Hibernate
    ├─ Generate INSERT SQL
    ├─ Execute with Hibernate
    └─ Return managed entity
  │
  Database
    └─ INSERT INTO pgs (...) VALUES (...)
       RETURNING id, created_at, updated_at, ...
```

### Update Pattern
```
User Request: PUT /v1/pg/{pg_id}
             { "name": "Updated Name", ... }
  │
  Controller
    ├─ Extract @PathVariable UUID pg_id
    ├─ Validate @Valid @RequestBody PgUpdateRequest
    ├─ Call pgService.updatePg(pg_id, request)
    └─ Return ResponseEntity.ok(response)
  │
  Service (Transactional)
    ├─ Fetch existing Pg from repository
    ├─ Check if not found -> throw ResourceNotFoundException
    ├─ Update only non-null fields from request:
    │   if (request.getName() != null) {
    │     pg.setName(request.getName());
    │   }
    ├─ Call pgRepository.save(pg)
    ├─ Map to PgDetailResponse
    └─ Return response
  │
  Repository + Hibernate
    ├─ Detect changes via managed entity
    ├─ Generate UPDATE SQL for changed fields
    └─ Execute
  │
  Database
    └─ UPDATE pgs SET name = ? WHERE id = ?
       RETURNING ...
```

---

## Configuration Architecture

```
Spring Boot Auto-Configuration
  ↓
Application-specific Configurations:

1. SecurityConfig
   ├─ PasswordEncoder (BCryptPasswordEncoder)
   ├─ SecurityFilterChain
   ├─ Filter ordering
   └─ Public route whitelisting

2. JwtConfig
   ├─ Access token expiration
   ├─ Refresh token expiration
   └─ Secret key (from environment)

3. CorsConfig
   ├─ Allowed origins
   ├─ Allowed methods (GET, POST, etc.)
   └─ Allowed headers

4. WebConfig
   ├─ Date/time formatting
   └─ Custom serialization

5. AsyncConfig
   ├─ Thread pool size
   └─ Async task execution

6. RedisConfig (if enabled)
   ├─ Cache configuration
   └─ Session management

7. MailConfig (if enabled)
   ├─ SMTP settings
   └─ Email templates

8. S3Config (if enabled)
   ├─ AWS credentials
   └─ File upload settings

9. SwaggerConfig
   ├─ OpenAPI documentation
   ├─ Bearer scheme definition
   └─ Endpoint documentation
```

---

## Deployment Architecture

```
Development Environment
  ├─ application-dev.yml
  └─ Local PostgreSQL (docker-compose.yml)

Production Environment
  ├─ application-prod.yml
  └─ Remote PostgreSQL server

Build & Package
  ├─ mvn clean install
  ├─ Generates pg-backend-1.0.jar
  └─ Spring Boot executable JAR

Deployment Options
  ├─ Docker container
  │   └─ Dockerfile (Dockerfile in repo)
  ├─ Kubernetes (not configured yet)
  ├─ AWS EC2 / ECS
  └─ Traditional VM / physical server

Environment Variables (production)
  ├─ SPRING_DATASOURCE_URL
  ├─ SPRING_DATASOURCE_USERNAME
  ├─ SPRING_DATASOURCE_PASSWORD
  ├─ JWT_SECRET_KEY
  ├─ JWT_ACCESS_TOKEN_EXPIRATION
  └─ ... others
```

