# PG Management System - Quick Reference Guide

## Key Facts at a Glance

| Aspect | Details |
|--------|---------|
| **Framework** | Spring Boot 3.5.7 |
| **Language** | Java 21 |
| **Build Tool** | Maven |
| **Database** | PostgreSQL with Flyway migrations |
| **Auth** | JWT (JJWT 0.12.6) + Spring Security |
| **Documentation** | OpenAPI 3 / Swagger |
| **ORM** | JPA/Hibernate |
| **Code Generation** | Lombok |

---

## Project Layout Quick Map

```
src/main/java/org/recnos/pg/
├── controller/          ← REST APIs (organized by feature: admin, auth, pg, user, payment, etc.)
├── service/             ← Business logic
├── repository/          ← Data access (JpaRepository interfaces)
├── model/
│   ├── entity/          ← Database entities
│   ├── dto/
│   │   ├── request/     ← Input validation DTOs
│   │   └── response/    ← Output DTOs
│   └── enums/           ← Status/type enums
├── mapper/              ← Entity → DTO converters
├── security/            ← JWT filters & auth
├── exception/           ← Custom exceptions & global handler
├── config/              ← Spring configurations
├── constants/           ← Application constants
├── validator/           ← Custom validators
├── event/               ← Application events
└── listener/            ← Event listeners
```

---

## Essential Patterns for PgController

### 1. Class Declaration
```java
@RestController
@RequestMapping("v1/pg")
@RequiredArgsConstructor
@Tag(name = "PG Management", description = "APIs for PG listings")
@SecurityRequirement(name = "bearerAuth")
public class PgController {
    private final PgService pgService;
```

### 2. Read Operations
```java
@GetMapping
public ResponseEntity<Page<PgListResponse>> listPgs(
    @RequestParam(required = false) String city,
    @RequestParam(required = false) String status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    Page<PgListResponse> result = pgService.listPgs(city, status, page, size);
    return ResponseEntity.ok(result);
}

@GetMapping("/{pg_id}")
public ResponseEntity<PgDetailResponse> getPgById(@PathVariable UUID pg_id) {
    PgDetailResponse response = pgService.getPgById(pg_id);
    return ResponseEntity.ok(response);
}
```

### 3. Create Operation
```java
@PostMapping
public ResponseEntity<PgDetailResponse> createPg(@Valid @RequestBody PgCreateRequest request) {
    PgDetailResponse response = pgService.createPg(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 4. Update Operation
```java
@PutMapping("/{pg_id}")
public ResponseEntity<PgDetailResponse> updatePg(
    @PathVariable UUID pg_id,
    @Valid @RequestBody PgUpdateRequest request
) {
    PgDetailResponse response = pgService.updatePg(pg_id, request);
    return ResponseEntity.ok(response);
}
```

### 5. Delete Operation
```java
@DeleteMapping("/{pg_id}")
public ResponseEntity<Void> deletePg(@PathVariable UUID pg_id) {
    pgService.deletePg(pg_id);
    return ResponseEntity.noContent().build();
}
```

---

## Service Layer Template

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PgService {

    private final PgRepository pgRepository;
    private final PgMapper pgMapper;
    private final OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public Page<PgListResponse> listPgs(String city, String status, int page, int size) {
        // Implement pagination with filters
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Pg> pgs = pgRepository.findAll(pageable);
        return pgs.map(pgMapper::toListResponse);
    }

    @Transactional(readOnly = true)
    public PgDetailResponse getPgById(UUID pgId) {
        Pg pg = pgRepository.findById(pgId)
            .orElseThrow(() -> new ResourceNotFoundException("PG not found with id: " + pgId));
        return pgMapper.toDetailResponse(pg);
    }

    @Transactional
    public PgDetailResponse createPg(PgCreateRequest request) {
        // Validation
        Owner owner = ownerRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        // Entity creation
        Pg pg = new Pg();
        pg.setOwner(owner);
        pg.setName(request.getName());
        // ... map all fields

        Pg saved = pgRepository.save(pg);
        return pgMapper.toDetailResponse(saved);
    }

    @Transactional
    public PgDetailResponse updatePg(UUID pgId, PgUpdateRequest request) {
        Pg pg = pgRepository.findById(pgId)
            .orElseThrow(() -> new ResourceNotFoundException("PG not found"));

        // Authorization check if needed
        if (request.getName() != null) {
            pg.setName(request.getName());
        }
        // ... update other fields conditionally

        Pg updated = pgRepository.save(pg);
        return pgMapper.toDetailResponse(updated);
    }

    @Transactional
    public void deletePg(UUID pgId) {
        Pg pg = pgRepository.findById(pgId)
            .orElseThrow(() -> new ResourceNotFoundException("PG not found"));
        pgRepository.delete(pg);
    }
}
```

---

## DTO Templates

### Request DTO
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgCreateRequest {

    @NotBlank(message = "PG name is required")
    @Size(min = 3, max = 255, message = "Name must be 3-255 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String contactPhone;

    private String description;
    private String propertyType;
    // ... other fields with appropriate validation
}
```

### Response DTO
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgDetailResponse {

    private UUID id;
    private String name;
    private String slug;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String genderType;
    private String status;
    private String approvalStatus;
    private Boolean isFeatured;
    private Integer viewCount;
    private Integer favoriteCount;
    
    private OwnerProfileResponse owner;
    private List<PgImageResponse> images;
    private List<AmenityResponse> amenities;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## Repository Pattern

```java
@Repository
public interface PgRepository extends JpaRepository<Pg, UUID> {

    Optional<Pg> findBySlug(String slug);

    Page<Pg> findByCity(String city, Pageable pageable);

    Page<Pg> findByStatus(String status, Pageable pageable);

    Page<Pg> findByCityAndStatus(String city, String status, Pageable pageable);

    List<Pg> findByOwerId(UUID ownerId);

    boolean existsBySlug(String slug);
}
```

---

## Exception Handling

Always throw custom exceptions in service layer:

```java
// Not found
throw new ResourceNotFoundException("PG not found with id: " + pgId);

// Invalid input
throw new BadRequestException("Invalid PG status");

// Access denied
throw new ForbiddenException("You can only access your own PGs");

// Duplicate
throw new DuplicateResourceException("PG slug already exists");
```

GlobalExceptionHandler automatically converts to appropriate HTTP responses.

---

## Security

### Protected Endpoint (default)
All endpoints except `/v1/auth/**` require `Authorization: Bearer {JWT_TOKEN}` header

### Get Current User
```java
UUID currentUserId = SecurityContextHolder.getCurrentUserId();
```

### Permission Checks
```java
// In service
UUID currentUserId = SecurityContextHolder.getCurrentUserId();
if (!currentUserId.equals(userId)) {
    throw new ForbiddenException("Unauthorized access");
}
```

---

## Database Entities - Key Points

### Pg Entity
- **ID**: UUID primary key
- **Status**: draft, pending, approved, blocked, archived
- **Timestamps**: createdAt, updatedAt (auto-managed)
- **Metrics**: viewCount, favoriteCount, contactClickCount, visitRequestCount
- **Relations**: ManyToOne with Owner (on delete cascade)
- **JSON columns**: nearbyLocations, foodPlans, foodPricing

### Owner Entity
- **ID**: UUID primary key
- **Verification**: isVerified, verificationStatus (pending/approved/rejected)
- **Security**: passwordHash (BCrypt), loginAttempts, lockedUntil
- **Trust**: trustScore, complaintCount, visitConversionRate
- **Timestamps**: createdAt, updatedAt (auto-managed)

---

## Testing Quick Reference

Test classes mirror main structure:
```
src/test/java/org/recnos/pg/
├── controller/
│   └── pg/
│       └── PgControllerTest.java
├── service/
│   └── pg/
│       └── PgServiceTest.java
└── repository/
    └── PgRepositoryTest.java
```

---

## Common Operations

### List with Pagination
```java
Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
Page<Pg> result = pgRepository.findAll(pageable);
return result.map(pgMapper::toListResponse);
```

### List with Filter
```java
Pageable pageable = PageRequest.of(page, size);
Page<Pg> result = pgRepository.findByCityAndStatus(city, status, pageable);
```

### Find or Throw
```java
Pg pg = pgRepository.findById(pgId)
    .orElseThrow(() -> new ResourceNotFoundException("PG not found"));
```

### Update Entity
```java
// Only update non-null fields from request
if (request.getName() != null) {
    pg.setName(request.getName());
}
// ... repeat for other fields
pgRepository.save(pg);
```

---

## API Endpoint Conventions

- **GET** `/v1/pg` - List all (with filters & pagination)
- **GET** `/v1/pg/{id}` - Get one
- **POST** `/v1/pg` - Create (201 Created)
- **PUT** `/v1/pg/{id}` - Update (200 OK)
- **DELETE** `/v1/pg/{id}` - Delete (204 No Content)
- **POST** `/v1/pg/{id}/action` - Custom action

---

## Swagger/OpenAPI Tags

Add to controller class:
```java
@Tag(
    name = "PG Management",
    description = "APIs for managing PG listings and properties"
)
```

Add to protected methods:
```java
@SecurityRequirement(name = "bearerAuth")
```

---

## Key Files Reference

| Purpose | Path |
|---------|------|
| Main App | `PGApplication.java` |
| Security Config | `config/SecurityConfig.java` |
| JWT Auth | `security/JwtAuthenticationFilter.java` |
| Exception Handler | `exception/GlobalExceptionHandler.java` |
| Error Response | `model/dto/response/ErrorResponse.java` |
| Constants | `constants/AppConstants.java` |
| Database Migrations | `src/main/resources/db/migration/` |

