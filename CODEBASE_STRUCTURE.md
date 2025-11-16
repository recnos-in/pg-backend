# PG Management System - Codebase Structure & Architecture Overview

## Executive Summary

This is a **Spring Boot 3.5.7** backend application for a Paying Guest (PG) Management System built with Java 21. The project follows a layered architecture pattern with clear separation of concerns using Controllers, Services, DTOs, and Repositories. It uses PostgreSQL as the database with Flyway for migrations and JPA/Hibernate for ORM.

---

## 1. PROJECT STRUCTURE

### Root Directory Organization

```
pg-backend/
├── src/
│   ├── main/
│   │   ├── java/org/recnos/pg/
│   │   │   ├── PGApplication.java              # Main Spring Boot application
│   │   │   ├── config/                         # Configuration classes
│   │   │   ├── constants/                      # Application constants
│   │   │   ├── controller/                     # REST controllers (organized by module)
│   │   │   ├── mapper/                         # Entity to DTO mappers
│   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/               # Request DTOs
│   │   │   │   │   └── response/              # Response DTOs
│   │   │   │   ├── entity/                    # JPA entities
│   │   │   │   ├── enums/                     # Enum classes
│   │   │   │   └── response/                  # Generic response wrappers
│   │   │   ├── repository/                    # Data access layer (JPA repositories)
│   │   │   ├── service/                       # Business logic (organized by module)
│   │   │   ├── security/                      # JWT authentication & security
│   │   │   ├── exception/                     # Custom exceptions & global handler
│   │   │   ├── event/                         # Application events
│   │   │   ├── listener/                      # Event listeners
│   │   │   ├── validator/                     # Custom validators
│   │   │   ├── specification/                 # JPA specifications for queries
│   │   │   ├── util/                          # Utility classes
│   │   │   ├── scheduler/                     # Scheduled tasks
│   │   │   └── migration/                     # Database migrations
│   │   └── resources/
│   │       ├── application.yml                # Main config
│   │       ├── application-dev.yml            # Development config
│   │       ├── application-prod.yml           # Production config
│   │       ├── db/migration/                  # Flyway SQL migrations
│   │       └── templates/                     # Email templates
│   └── test/                                   # Test classes mirroring main structure
├── pom.xml                                      # Maven dependencies & build config
├── docker-compose.yml                          # Docker setup for PostgreSQL
└── docs/                                        # Documentation & API specs
```

---

## 2. FRAMEWORK & TECH STACK

### Core Framework
- **Spring Boot**: 3.5.7
- **Java Version**: 21
- **Build Tool**: Maven 3
- **Application Type**: RESTful Web Service

### Key Dependencies

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot Web | 3.5.7 | REST API development |
| Spring Data JPA | 3.5.7 | Database access & ORM |
| Spring Security | 3.5.7 | Authentication & authorization |
| PostgreSQL Driver | 42.7.5 | Database driver |
| Flyway | 11.1.0 | Database migrations |
| Lombok | 1.18.42 | Code generation (getters, setters, constructors) |
| JJWT (JWT) | 0.12.6 | JSON Web Token handling |
| SpringDoc OpenAPI | 2.8.7 | Swagger/OpenAPI 3 documentation |
| Hibernate Validator | 8.0.2 | Bean validation |
| Jackson | 2.18.2 | JSON serialization/deserialization |

### Database
- **PostgreSQL**: Latest version (see docker-compose.yml)
- **Extensions Enabled**: 
  - UUID extension (uuid-ossp)
  - PostGIS (for geospatial queries)
- **Migration Tool**: Flyway (version 11.1.0)

---

## 3. ARCHITECTURE PATTERNS

### Layered Architecture

```
┌─────────────────────────────────────────────────┐
│          REST Controller Layer (HTTP)           │
│  Handles incoming requests, validation, routing │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│         Service Layer (Business Logic)          │
│  Core business rules, transactions, workflows   │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│         Repository Layer (Data Access)          │
│  CRUD operations, custom queries (JPA)          │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│      Database Layer (PostgreSQL)                │
│       Persistent data storage                   │
└─────────────────────────────────────────────────┘
```

### Cross-cutting Concerns

- **Security**: JWT-based authentication via JwtAuthenticationFilter
- **Exception Handling**: Global exception handler with custom exceptions
- **Validation**: Jakarta validation annotations + custom validators
- **Mapping**: Manual entity-to-DTO mappers (Component-based)
- **Events**: Application event publishing for async operations
- **Configuration**: Spring configuration classes for security, CORS, Async, etc.

---

## 4. EXISTING CONTROLLERS & PATTERNS

### Controller Locations & Patterns

Controllers are organized by business domain/module under `org.recnos.pg.controller.*`:

```
controller/
├── admin/
│   ├── AdminAnalyticsController
│   ├── AdminAuthController
│   ├── AdminDashboardController
│   ├── AdminOwnerController
│   ├── AdminPgController
│   ├── AdminUserController
│   ├── ApprovalController
│   ├── ContentManagementController
│   └── SystemSettingsController
├── auth/
│   ├── AdminAuthController
│   ├── AuthController
│   ├── OwnerAuthController
│   └── UserAuthController
├── content/
│   ├── BlogController
│   ├── FinestPgController
│   └── PopularCityController
├── notification/
│   └── NotificationController
├── owner/
│   ├── CallbackController
│   ├── OwnerAnalyticsController
│   ├── OwnerController
│   ├── OwnerProfileController
│   ├── OwnerVisitController
│   ├── PgManagementController
│   └── SubscriptionController
├── payment/
│   ├── InvoiceController
│   ├── PaymentController
│   └── PaymentWebhookController
├── pg/
│   ├── PgController                (EMPTY - needs implementation)
│   ├── PgDetailController
│   ├── PgSearchController
│   ├── ReviewController
│   └── PgSearchController
└── user/
    ├── FavoriteController
    ├── UserController              (IMPLEMENTED - see pattern below)
    ├── UserProfileController
    └── VisitController
```

### Actual Implemented Controller Pattern (UserController)

**File**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/controller/user/UserController.java`

```java
@RestController
@RequestMapping("v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles and preferences")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable("user_id") UUID userId) {
        UserProfileResponse profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/{user_id}/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping("/{user_id}/preferences")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PreferencesUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updatePreferences(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
```

### Controller Pattern Conventions

1. **Class-Level Annotations**:
   - `@RestController`: Indicates this is a REST controller
   - `@RequestMapping("v1/resource")`: API version & base path
   - `@RequiredArgsConstructor`: Lombok annotation for constructor injection
   - `@Tag(name = "...", description = "...")`: OpenAPI/Swagger documentation
   - `@SecurityRequirement(name = "bearerAuth")`: Indicates JWT auth required

2. **Method-Level Patterns**:
   - Use `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
   - Path parameters: `@PathVariable("param_name")`
   - Request body: `@Valid @RequestBody DTOClass request`
   - Return `ResponseEntity<DTO>` for flexibility
   - HTTP Status codes: 200 OK, 201 CREATED, 204 NO CONTENT, etc.

3. **Dependency Injection**:
   - Use constructor injection via Lombok's `@RequiredArgsConstructor`
   - Inject only services, not repositories

4. **Response Format**:
   - Simple responses: `ResponseEntity<DTOClass>`
   - Success messages: `Map.of("message", "...")`
   - Error handling: Delegated to GlobalExceptionHandler

---

## 5. DATABASE SETUP & ORM

### Database Details

- **Type**: PostgreSQL
- **Connection**: JDBC
- **Dialect**: PostgreSQL Dialect (Hibernate)
- **Connection Pool**: Default Spring DataSource

### JPA/Hibernate Configuration

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate    # Validates schema, doesn't create/update
    show-sql: true          # Logs SQL queries
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true    # Pretty-prints SQL
```

### Database Migrations

- **Tool**: Flyway (version 11.1.0)
- **Location**: `src/main/resources/db/migration/`
- **Naming**: `V[number]__[description].sql`

**Existing Migrations**:
- `V1__init_schema.sql` - Initial schema with all core tables
- `V2__add_amenities.sql` - Amenities feature
- `V3__add_subscription_plans.sql` - Subscription system
- `V4__add_email_templates.sql` - Email templates
- `V5__add_indexes.sql` - Performance indexes

### Key Database Tables

#### Core Entities
- **users** - Regular user accounts
- **owners** - PG owner accounts
- **admins** - Admin accounts
- **pgs** - PG property listings
- **pg_rooms** - Room details within a PG
- **pg_images** - Images for PGs
- **amenities** & **pg_amenities** - Features offered

#### User Interactions
- **favorites** - Bookmarked PGs
- **visits** - Scheduled property visits
- **callbacks** - Callback requests
- **reviews** - User reviews/ratings

#### Business
- **subscription_plans** - Owner subscription tiers
- **subscriptions** - Active owner subscriptions
- **payments** - Payment records
- **invoices** - Generated invoices

---

## 6. EXISTING MODELS & ENTITIES

### Entity Locations

```
model/
├── entity/
│   ├── User.java              # Regular user account
│   ├── Owner.java             # PG owner account
│   ├── Admin.java             # Admin account
│   ├── Pg.java                # PG property listing
│   ├── PgRoom.java            # Room in a PG
│   ├── PgImage.java           # Images for PG
│   ├── Amenity.java           # Amenity type
│   ├── PgAmenity.java         # PG amenities (junction)
│   ├── Favorite.java          # User favorite PG
│   ├── Visit.java             # Property visit request
│   ├── Callback.java          # Callback request
│   ├── Review.java            # User review
│   ├── Subscription.java      # Owner subscription
│   ├── SubscriptionPlan.java  # Subscription plan
│   ├── Payment.java           # Payment record
│   ├── Invoice.java           # Invoice
│   ├── Notification.java      # Notifications
│   └── ...
├── dto/
│   ├── request/               # Request DTOs
│   └── response/              # Response DTOs
├── enums/
│   ├── PgStatus.java          # PG status enum
│   ├── VisitStatus.java       # Visit status enum
│   ├── SubscriptionStatus.java
│   ├── FoodType.java
│   └── ...
└── response/
    ├── ApiResponse.java       # Generic API response wrapper
    ├── ErrorResponse.java     # Error response format
    └── PaginatedResponse.java # Pagination wrapper
```

### Key Entity Example: Pg.java

**Location**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/model/entity/Pg.java`

**Key Characteristics**:
- UUID as primary key (auto-generated)
- Relationships:
  - `@ManyToOne` with Owner (owner_id)
  - `@ManyToOne` with Admin (approved_by)
- JSON columns:
  - `nearbyLocations` - Stored as JSON in DB
  - `foodPlans` - JSON structure
  - `foodPricing` - JSON structure
- Timestamps:
  - `createdAt` - Auto-set on creation
  - `updatedAt` - Auto-set on creation & update
- Status tracking:
  - `status` - Draft, pending, approved, blocked, archived
  - `approvalStatus` - Pending, approved, rejected
  - `isArchived`, `isDeleted` - Soft delete flags
- Metrics tracking:
  - `viewCount`, `favoriteCount`, `contactClickCount`, etc.
- Indexes on:
  - owner_id, slug, city, status, gender_type, location, is_featured

### User Entity Key Fields

**Location**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/model/entity/User.java`

**Key Characteristics**:
- UUID primary key
- Unique constraints on email, mobile, googleId
- Security fields:
  - `passwordHash` (BCrypt encoded)
  - `loginAttempts`, `lockedUntil` (brute-force protection)
  - `mfaEnabled`, `mfaSecret` (2FA support)
- Verification:
  - `isEmailVerified`, `isMobileVerified`
  - `emailVerificationToken`, `emailVerificationExpires`
  - `passwordResetToken`, `passwordResetExpires`
- Preferences:
  - `preferredLocations` (text array)
  - `budgetMin`, `budgetMax` (BigDecimal)
  - `moveInDate` (LocalDate)
- Account control:
  - `isBlocked` with reason and timestamp
  - `lastLogin` timestamp
- Timestamps with `@PrePersist` & `@PreUpdate` lifecycle hooks

---

## 7. ROUTE CONFIGURATION

### API Versioning & Base Paths

All APIs follow versioning scheme: `/v1/{resource}`

**Existing Route Patterns**:

```
POST   /v1/auth/user/register                    - User registration
POST   /v1/auth/user/login                       - User login
POST   /v1/auth/owner/register                   - Owner registration
POST   /v1/auth/owner/login                      - Owner login

GET    /v1/user/me                               - Current user profile
GET    /v1/user/{user_id}                        - Get user by ID
PUT    /v1/user/{user_id}                        - Update user
PUT    /v1/user/{user_id}/password               - Change password
PUT    /v1/user/{user_id}/preferences            - Update preferences

GET    /v1/pg                                    - List PGs (search/filter)
GET    /v1/pg/{pg_id}                            - Get PG details
POST   /v1/owner/pg                              - Create PG (owner)
PUT    /v1/owner/pg/{pg_id}                      - Update PG (owner)
DELETE /v1/owner/pg/{pg_id}                      - Delete PG (owner)

POST   /v1/user/visit                            - Schedule PG visit
GET    /v1/owner/visits                          - Get visit requests (owner)
PUT    /v1/owner/visits/{visit_id}               - Respond to visit

GET    /v1/pg/{pg_id}/reviews                    - Get reviews
POST   /v1/pg/{pg_id}/reviews                    - Create review

POST   /v1/user/favorites/{pg_id}                - Add favorite
DELETE /v1/user/favorites/{pg_id}                - Remove favorite
GET    /v1/user/favorites                        - Get favorites

POST   /v1/payment/initiate                      - Initiate payment
POST   /v1/payment/webhook                       - Payment webhook

GET    /admin/users                              - List users (admin)
GET    /admin/pgs                                - List PGs (admin)
POST   /admin/pgs/{pg_id}/approve                - Approve PG
POST   /admin/pgs/{pg_id}/reject                 - Reject PG
```

### Security Configuration

**File**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v1/auth/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**Key Security Features**:
- CSRF disabled (stateless API)
- Stateless session management
- JWT authentication via custom filter
- Public endpoints: `/v1/auth/**`, Swagger docs, Actuator
- All other endpoints require authentication

---

## 8. MIDDLEWARE & VALIDATION PATTERNS

### JWT Authentication Filter

**File**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/security/JwtAuthenticationFilter.java`

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userIdString = jwtService.extractUserId(jwt);
            final UUID userId = UUID.fromString(userIdString);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!jwtService.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userIdString,
                            null,
                            new ArrayList<>()
                        );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
```

### Validation Patterns

#### 1. Jakarta Validation Annotations (in DTOs)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobile;

    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    private List<String> preferredLocations;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private LocalDate moveInDate;
}
```

#### 2. Service Layer Validation

```java
@Service
@RequiredArgsConstructor
public class UserService {

    @Transactional
    public UserProfileResponse updateUser(UUID userId, UserUpdateRequest request) {
        UUID currentUserId = SecurityContextHolder.getCurrentUserId();

        // Authorization check
        if (!currentUserId.equals(userId)) {
            throw new ForbiddenException("You can only update your own profile");
        }

        User user = findById(userId);

        // Duplicate check
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Conditional updates
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toProfileResponse(updatedUser);
    }
}
```

#### 3. Global Exception Handling

**File**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/exception/GlobalExceptionHandler.java`

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex, WebRequest request) {
        // Similar pattern...
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        // Return errors with field-level details
    }
}
```

### Custom Exceptions

**Location**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/exception/`

Available exceptions:
- `ResourceNotFoundException` (404)
- `BadRequestException` (400)
- `UnauthorizedException` (401)
- `ForbiddenException` (403)
- `InvalidCredentialsException` (401)
- `DuplicateResourceException` (409 Conflict)
- `TokenExpiredException` (401)
- `FileStorageException` (500)
- `PaymentException` (402 Payment Required)

All extend `RuntimeException` for simplified transaction handling.

### DTO Validation Pattern

**Request DTO Example**:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email or mobile is required")
    private String emailOrMobile;

    @NotBlank(message = "Password is required")
    private String password;
}
```

**Response DTO Example**:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private UserProfileResponse user;
    private TokenResponse tokens;
}
```

**Error Response Format**:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;  // For validation errors
}
```

---

## 9. MAPPER PATTERN

Mappers are component-based for manual control and flexibility.

**Location**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/mapper/`

**Example: UserMapper**

```java
@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .gender(user.getGender())
                .occupation(user.getOccupation())
                .preferredLocations(user.getPreferredLocations())
                .budgetMin(user.getBudgetMin())
                .budgetMax(user.getBudgetMax())
                .moveInDate(user.getMoveInDate())
                .isEmailVerified(user.getIsEmailVerified())
                .isMobileVerified(user.getIsMobileVerified())
                .mfaEnabled(user.getMfaEnabled())
                .googleId(user.getGoogleId())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
```

**Existing Mappers**:
- `UserMapper` - User entity to DTO
- `OwnerMapper` - Owner entity to DTO
- `PgMapper` - PG entity to DTO
- `PaymentMapper` - Payment entity to DTO
- `VisitMapper` - Visit entity to DTO
- `NotificationMapper` - Notification entity to DTO

---

## 10. SERVICE LAYER PATTERN

Services handle all business logic and transactions.

**Location**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/service/`

**Example: AuthService**

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Duplicate checks
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Entity creation
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsEmailVerified(false);
        // ... other initializations

        User savedUser = userRepository.save(user);

        // Token generation
        TokenResponse tokens = generateTokens(savedUser.getId());

        return RegisterResponse.builder()
                .message("User registered successfully")
                .user(userMapper.toProfileResponse(savedUser))
                .tokens(tokens)
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Find user
        User user = userRepository.findByEmail(request.getEmailOrMobile())
                .or(() -> userRepository.findByMobile(request.getEmailOrMobile()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Validation checks
        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            throw new InvalidCredentialsException("Account is blocked");
        }

        // Password verification
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Update login state
        user.setLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        TokenResponse tokens = generateTokens(user.getId());

        return LoginResponse.builder()
                .message("Login successful")
                .user(userMapper.toProfileResponse(user))
                .tokens(tokens)
                .build();
    }
}
```

**Service Characteristics**:
- `@Service` annotation
- Constructor injection via `@RequiredArgsConstructor`
- `@Transactional` for database operations (read-only where applicable)
- Proper exception handling
- Null checks and validations
- Use mappers for DTO conversion
- Call only repository methods, not direct queries

---

## 11. REPOSITORY PATTERN

Repositories extend `JpaRepository` for CRUD operations.

**Example: UserRepository**

```java
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}
```

**Key Patterns**:
- Extend `JpaRepository<Entity, ID>`
- Use `Optional<T>` return types
- Named query methods (Spring Data derives SQL)
- Custom boolean exists methods for validation

---

## 12. KEY CONFIGURATIONS

### Swagger/OpenAPI Configuration

**File**: `config/SwaggerConfig.java`

- Enables `/swagger-ui.html` and `/v3/api-docs`
- Controllers use `@Tag` for grouping
- Methods use `@SecurityRequirement(name = "bearerAuth")` for protected endpoints

### Security Context Holder

**File**: `/Users/kavishankarks/Documents/GitHub/pg-backend/src/main/java/org/recnos/pg/security/SecurityContextHolder.java`

Utility to extract current user ID from Spring Security context:

```java
public class SecurityContextHolder {
    public static UUID getCurrentUserId() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication();
        return UUID.fromString((String) auth.getPrincipal());
    }
}
```

### Configuration Classes

- **SecurityConfig**: Spring Security setup, password encoding, JWT filter
- **JwtConfig**: JWT token expiration times
- **CorsConfig**: CORS settings for frontend
- **AsyncConfig**: Async task execution
- **RedisConfig**: Cache configuration
- **MailConfig**: Email sending setup
- **S3Config**: AWS S3 storage integration
- **WebConfig**: General web configuration

---

## SUMMARY TABLE: PATTERNS TO FOLLOW FOR PgController

| Aspect | Pattern | Example |
|--------|---------|---------|
| **Class Declaration** | @RestController, @RequestMapping, @RequiredArgsConstructor, @Tag | `@RestController @RequestMapping("v1/pg")` |
| **Dependency** | Constructor inject UserService via @RequiredArgsConstructor | `private final PgService pgService;` |
| **GET Single** | @GetMapping("/{id}") returning ResponseEntity<DTO> | `@GetMapping("/{pg_id}")` |
| **GET List** | @GetMapping with optional params for filter/pagination | `@GetMapping(?city=&status=)` |
| **CREATE** | @PostMapping, @Valid @RequestBody, return 201 CREATED | `@PostMapping @Valid @RequestBody` |
| **UPDATE** | @PutMapping("/{id}"), @Valid @RequestBody | `@PutMapping("/{pg_id}")` |
| **DELETE** | @DeleteMapping("/{id}"), return 204 NO_CONTENT or success message | `@DeleteMapping("/{pg_id}")` |
| **Validation** | @Valid on DTO, GlobalExceptionHandler catches violations | MethodArgumentNotValidException handler |
| **Security** | @SecurityRequirement annotation, JWT filter validates token | All non-auth endpoints require Bearer token |
| **Error Response** | Throw custom exceptions, GlobalExceptionHandler converts to ErrorResponse | throw new ResourceNotFoundException(...) |
| **DTOs** | Request: Lombok @Data/@Builder, Response: Same with additional fields | UserUpdateRequest, UserProfileResponse |
| **Mappers** | @Component mapper classes with toDTO methods | userMapper.toProfileResponse(user) |
| **Transactions** | @Transactional at service method level | @Transactional public returnType methodName() |
| **Response Format** | ResponseEntity.ok(dto) or ResponseEntity.status().body() | ResponseEntity.ok(profileResponse) |

---

## READY TO IMPLEMENT PgController

The project is now ready for implementing the `PgController` following the established patterns:

1. **Controller class** with proper annotations and Swagger docs
2. **Service layer** handling business logic and transactions
3. **Repository** for database queries
4. **Request/Response DTOs** with proper validation
5. **Mapper** for entity-to-DTO conversion
6. **Exception handling** through existing global handler
7. **Security** via JWT authentication filter

All supporting infrastructure is already in place and configured.

