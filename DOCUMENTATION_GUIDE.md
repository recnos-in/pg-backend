# PG Backend - Documentation Guide

This project includes comprehensive documentation to help you understand the codebase structure and implement new features following existing patterns.

## Documentation Files

### 1. **CODEBASE_STRUCTURE.md** (35KB - Comprehensive Reference)
A detailed, complete overview of the entire project structure.

**Contents:**
- Executive summary (framework, tech stack)
- Complete project directory structure
- Framework and dependencies breakdown
- Architecture patterns and layered architecture
- Existing controllers and their patterns with code examples
- Database setup and ORM configuration
- Entity models with detailed field explanations
- Route configuration and API endpoints
- Middleware and validation patterns with code
- Mapper pattern implementation
- Service layer patterns with full examples
- Repository patterns
- Key configurations
- Complete patterns table for PgController implementation

**Best for:** Deep understanding of the entire system, reference when implementing major features

---

### 2. **QUICK_REFERENCE.md** (11KB - Developer Cheat Sheet)
Fast reference guide for common tasks and patterns.

**Contents:**
- Key facts at a glance (framework, language, database, etc.)
- Project layout quick map
- Essential patterns for PgController (5 templates)
- Service layer template
- DTO templates (request and response)
- Repository pattern example
- Exception handling guide
- Security quick reference
- Database entities key points
- Testing quick reference
- Common operations code snippets
- API endpoint conventions
- Swagger/OpenAPI tags
- Key files reference table

**Best for:** Quick lookups while coding, implementing features, copy-paste code templates

---

### 3. **ARCHITECTURE.md** (10KB - Visual Architecture)
System and component architecture with detailed diagrams.

**Contents:**
- Complete system architecture diagram (8 layers with descriptions)
- Request-response flow diagram
- Exception handling flow diagram
- Module architecture for major features (Auth, PG, User modules)
- Cross-cutting concerns (Security, Validation, Transactions)
- Data flow patterns (List, Create, Update patterns)
- Configuration architecture overview
- Deployment architecture

**Best for:** Understanding how components interact, debugging issues, system design discussions

---

## How to Use This Documentation

### Getting Started
1. Read **README.md** (project overview and setup)
2. Skim **QUICK_REFERENCE.md** (get familiar with basic patterns)
3. Deep dive into **CODEBASE_STRUCTURE.md** sections as needed

### For Implementation Tasks

#### Implementing PgController APIs
1. Check **CODEBASE_STRUCTURE.md** > Section 4: Controllers & Patterns
2. Use **QUICK_REFERENCE.md** > Essential Patterns for PgController (code templates)
3. Copy patterns from UserController as reference
4. Follow validation pattern from **CODEBASE_STRUCTURE.md** > Section 8

#### Understanding Service Layer
1. **CODEBASE_STRUCTURE.md** > Section 10: Service Layer Pattern
2. **QUICK_REFERENCE.md** > Service Layer Template
3. **ARCHITECTURE.md** > Data Flow Patterns

#### Working with DTOs
1. **CODEBASE_STRUCTURE.md** > Section 8: Validation Patterns
2. **QUICK_REFERENCE.md** > DTO Templates (Request & Response)
3. Look at existing DTOs in `/src/main/java/org/recnos/pg/model/dto/`

#### Database Operations
1. **CODEBASE_STRUCTURE.md** > Section 5: Database Setup & ORM
2. **CODEBASE_STRUCTURE.md** > Section 11: Repository Pattern
3. **QUICK_REFERENCE.md** > Common Operations

#### Error Handling
1. **CODEBASE_STRUCTURE.md** > Section 8: Exception Handling
2. **ARCHITECTURE.md** > Exception Handling Flow
3. Check GlobalExceptionHandler.java for available exceptions

#### Security & Authentication
1. **CODEBASE_STRUCTURE.md** > Section 8: JWT Authentication
2. **QUICK_REFERENCE.md** > Security section
3. **ARCHITECTURE.md** > Security Architecture

### For Code Reviews
- Reference **CODEBASE_STRUCTURE.md** > Summary table for expected patterns
- Check **ARCHITECTURE.md** > Module Architecture for consistency
- Verify against **QUICK_REFERENCE.md** > API Endpoint Conventions

### For Debugging
1. **ARCHITECTURE.md** > Request-Response Flow (trace the request path)
2. **ARCHITECTURE.md** > Exception Handling Flow (understand error handling)
3. **CODEBASE_STRUCTURE.md** > relevant section for the component

---

## Key Concepts Quick Reference

### Layered Architecture
```
Controller (HTTP) → Service (Business Logic) → Repository (Data Access) → Database
```

### Standard CRUD Pattern
- **GET /v1/resource** - List with pagination
- **GET /v1/resource/{id}** - Get single
- **POST /v1/resource** - Create (returns 201)
- **PUT /v1/resource/{id}** - Update (returns 200)
- **DELETE /v1/resource/{id}** - Delete (returns 204)

### Dependency Injection Order
```java
@RestController
@RequestMapping("v1/resource")
@RequiredArgsConstructor  // Auto-generates constructor
public class ResourceController {
    private final ResourceService resourceService;  // Injected via constructor
}
```

### Validation Layers
1. **DTO Level**: @NotBlank, @Email, @Pattern annotations
2. **Service Level**: Business rules (duplicates, authorization)
3. **Global Handler**: MethodArgumentNotValidException catches DTO validation

### Exception Handling
```
Service throws custom exception
  ↓
GlobalExceptionHandler catches it
  ↓
Converts to appropriate HTTP status code
  ↓
Returns ErrorResponse JSON
```

### Transaction Management
- Use `@Transactional` on service methods
- `@Transactional(readOnly = true)` for queries (optimized)
- Automatically rolls back on RuntimeException
- Mapped entities auto-update on save

---

## File Locations Quick Map

```
Important Files:
├── pom.xml - Dependencies (check for versions)
├── src/main/java/org/recnos/pg/
│   ├── PGApplication.java - Main Spring Boot entry
│   ├── controller/ - REST endpoints
│   ├── service/ - Business logic
│   ├── repository/ - Data access interfaces
│   ├── model/
│   │   ├── entity/ - Database entities
│   │   └── dto/ - Request/Response DTOs
│   ├── mapper/ - Entity → DTO converters
│   ├── exception/ - Custom exceptions & handler
│   ├── security/ - JWT & auth filters
│   └── config/ - Spring configurations
├── src/main/resources/
│   ├── application.yml - Base config
│   ├── application-dev.yml - Dev config
│   ├── application-prod.yml - Prod config
│   └── db/migration/ - Flyway SQL migrations
├── src/test/ - Test classes (mirror main structure)
├── docker-compose.yml - PostgreSQL setup
└── [This file] - DOCUMENTATION_GUIDE.md
```

---

## Common Tasks

### Add a New API Endpoint
1. Create controller method in appropriate controller
2. Create request DTO with @Valid annotations
3. Create response DTO with @Builder
4. Create service method with @Transactional
5. Add repository query method if needed
6. Add mapper method if converting entity to DTO
7. Handle exceptions (service throws custom exceptions)

### Add a New Entity
1. Create entity class in `model/entity/`
2. Add @Entity, @Table annotations
3. Add JPA relationships (@ManyToOne, @OneToMany)
4. Add validation annotations if needed
5. Create Flyway migration in `db/migration/`
6. Create repository interface extending JpaRepository
7. Create DTOs (request/response)
8. Create mapper (if not using same fields)

### Change Database Schema
1. Create new Flyway migration: `V[next_number]__description.sql`
2. Add migration file to: `src/main/resources/db/migration/`
3. Update entity classes to match new schema
4. Update DTOs if needed
5. Update repositories if new queries needed
6. Test migration locally with docker-compose

### Add Input Validation
1. For DTO fields: Add Jakarta validation annotations
   - @NotBlank, @Email, @Pattern, @Size, etc.
2. For business rules: Add checks in service layer
   - Check duplicates, authorization, business constraints
3. Throw appropriate custom exceptions if validation fails

### Debug an API Issue
1. Check controller method routing
2. Verify request DTO validation
3. Trace service method logic
4. Check repository query
5. Verify database state
6. Check exception handling (GlobalExceptionHandler)

---

## Swagger/API Documentation

Access live API documentation when app is running:
```
http://localhost:8080/swagger-ui.html
```

Or OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

Controllers automatically appear if:
1. Class has `@Tag(name = "...", description = "...")`
2. Methods have `@SecurityRequirement(name = "bearerAuth")` if protected

---

## Testing

Test classes mirror the main source structure:
```
src/test/java/org/recnos/pg/
├── controller/
│   └── [ControllerName]Test.java
├── service/
│   └── [ServiceName]Test.java
└── repository/
    └── [RepositoryName]Test.java
```

Example test class location:
- Main: `src/main/java/org/recnos/pg/controller/pg/PgController.java`
- Test: `src/test/java/org/recnos/pg/controller/pg/PgControllerTest.java`

---

## Running the Application

### Development
```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run JAR
java -jar target/pg-backend-1.0.jar
```

### Database Setup
```bash
# Start PostgreSQL with docker-compose
docker-compose up -d

# PostgreSQL will be available at:
# jdbc:postgresql://localhost:5432/pg_backend
# User: kavishankarks (from docker-compose.yml)
```

### Environment Configuration
- **Dev**: Uses `application-dev.yml`
- **Prod**: Uses `application-prod.yml`
- Set active profile: `SPRING_PROFILES_ACTIVE=dev` or `prod`

---

## Key Framework Versions

| Technology | Version |
|---|---|
| Spring Boot | 3.5.7 |
| Java | 21 |
| PostgreSQL Driver | 42.7.5 |
| Flyway | 11.1.0 |
| Lombok | 1.18.42 |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.7 |
| Hibernate Validator | 8.0.2 |
| Jackson | 2.18.2 |

---

## Next Steps

1. **Read QUICK_REFERENCE.md** for immediate practical patterns
2. **Review CODEBASE_STRUCTURE.md** Section 4 for controller examples
3. **Study an existing controller** (UserController) as a template
4. **Check ARCHITECTURE.md** to understand request flow
5. **Start implementing** - copy patterns from existing code
6. **Test your implementation** against the validation patterns

---

## Need Help?

### Understanding a Specific Component
- Check **CODEBASE_STRUCTURE.md** for that component
- Look at existing implementations in the codebase
- Reference **ARCHITECTURE.md** for how it connects to other components

### Following a Pattern
- See **QUICK_REFERENCE.md** for templates and code examples
- Check **CODEBASE_STRUCTURE.md** Summary Table (Section 12)
- Look at similar existing feature implementation

### Debugging
- Follow **ARCHITECTURE.md** > Request-Response Flow to trace the request
- Check exception type in **ARCHITECTURE.md** > Exception Handling Flow
- Verify against **CODEBASE_STRUCTURE.md** > Validation Patterns

---

**Last Updated**: November 16, 2025
**Documentation Version**: 1.0
