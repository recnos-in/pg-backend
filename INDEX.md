# PG Management System Backend - Complete Documentation Index

## Overview

You now have comprehensive documentation for the PG Management System backend. This is a Spring Boot 3.5.7 application using Java 21, PostgreSQL, JWT authentication, and a clean layered architecture.

---

## Documentation Files (Organized by Use Case)

### Starting Point
- **README.md** - Project setup, prerequisites, and how to run

### Getting Oriented (Read in This Order)
1. **DOCUMENTATION_GUIDE.md** - Master guide explaining all documentation and how to use it
2. **QUICK_REFERENCE.md** - Quick lookup sheet with code templates
3. **CODEBASE_STRUCTURE.md** - Detailed deep-dive reference
4. **ARCHITECTURE.md** - Visual diagrams and data flows

---

## Quick Navigation by Task

### I want to...

#### Understand the Overall System
- Start with: **DOCUMENTATION_GUIDE.md** (overview section)
- Then read: **ARCHITECTURE.md** (system architecture diagram)
- Reference: **CODEBASE_STRUCTURE.md** > Section 3

#### Implement PgController APIs
1. **QUICK_REFERENCE.md** > Essential Patterns for PgController (copy templates)
2. **CODEBASE_STRUCTURE.md** > Section 4: Controllers & Patterns
3. Look at UserController.java as live example
4. **CODEBASE_STRUCTURE.md** > Section 8: Validation Patterns

#### Create a New Service
1. **QUICK_REFERENCE.md** > Service Layer Template
2. **CODEBASE_STRUCTURE.md** > Section 10: Service Layer Pattern
3. **ARCHITECTURE.md** > Data Flow Patterns

#### Work with DTOs
1. **QUICK_REFERENCE.md** > DTO Templates
2. **CODEBASE_STRUCTURE.md** > Section 8: Validation Patterns
3. Check existing DTOs in `/src/main/java/org/recnos/pg/model/dto/`

#### Understand Database & ORM
1. **CODEBASE_STRUCTURE.md** > Section 5: Database Setup & ORM
2. **CODEBASE_STRUCTURE.md** > Section 6: Models & Entities
3. **CODEBASE_STRUCTURE.md** > Section 11: Repository Pattern

#### Add Security/Authorization
1. **QUICK_REFERENCE.md** > Security section
2. **CODEBASE_STRUCTURE.md** > Section 8: JWT Authentication
3. **ARCHITECTURE.md** > Security Architecture

#### Handle Errors
1. **QUICK_REFERENCE.md** > Exception Handling
2. **CODEBASE_STRUCTURE.md** > Section 8: Exception Handling
3. **ARCHITECTURE.md** > Exception Handling Flow

#### Debug an Issue
1. **ARCHITECTURE.md** > Request-Response Flow (trace request)
2. **ARCHITECTURE.md** > Exception Handling Flow (understand errors)
3. **CODEBASE_STRUCTURE.md** > relevant section

#### Write Tests
1. **QUICK_REFERENCE.md** > Testing Quick Reference
2. **DOCUMENTATION_GUIDE.md** > Testing section
3. Look at test structure under `/src/test/java/org/recnos/pg/`

#### Configure the Application
1. **CODEBASE_STRUCTURE.md** > Section 12: Key Configurations
2. **ARCHITECTURE.md** > Configuration Architecture
3. Check `/src/main/resources/application-*.yml`

#### Deploy to Production
1. **ARCHITECTURE.md** > Deployment Architecture
2. **DOCUMENTATION_GUIDE.md** > Running the Application

---

## File Statistics

| File | Size | Lines | Purpose |
|------|------|-------|---------|
| DOCUMENTATION_GUIDE.md | 11KB | 300+ | Master guide and task reference |
| QUICK_REFERENCE.md | 11KB | 380+ | Code templates and cheat sheet |
| CODEBASE_STRUCTURE.md | 35KB | 980+ | Comprehensive reference |
| ARCHITECTURE.md | 22KB | 590+ | Visual diagrams and flows |
| **Total** | **79KB** | **2,350+** | Complete documentation |

---

## Key Information at a Glance

### Framework Stack
- **Spring Boot**: 3.5.7
- **Language**: Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Auth**: JWT (JJWT) + Spring Security
- **ORM**: JPA/Hibernate
- **API Docs**: OpenAPI 3 / Swagger
- **Code Generation**: Lombok

### Architecture Pattern
```
HTTP Request
  ↓
Controller (routing, validation)
  ↓
Service (business logic, @Transactional)
  ↓
Repository (JPA data access)
  ↓
Database (PostgreSQL)
```

### Standard CRUD Endpoints
- `GET /v1/resource` - List (paginated)
- `GET /v1/resource/{id}` - Get single
- `POST /v1/resource` - Create (201)
- `PUT /v1/resource/{id}` - Update (200)
- `DELETE /v1/resource/{id}` - Delete (204)

### Exception Handling
All errors automatically converted to JSON response via GlobalExceptionHandler:
```json
{
  "timestamp": "2025-11-16T...",
  "status": 404,
  "error": "Not Found",
  "message": "PG not found with id: ...",
  "path": "/v1/pg/123",
  "validationErrors": {}
}
```

---

## Current Project Status

### Implemented Features
- User authentication (register, login)
- User profile management
- Owner authentication
- Owner profile management
- PG database entities and models
- Review system
- Visit scheduling
- Favorite/bookmark system
- Admin dashboard
- Payment system
- Subscription management
- Analytics
- Notifications

### Empty/TODO
- **PgController** - Needs implementation (you have templates!)
- **PgService** - Needs implementation
- **Additional features** - Can follow established patterns

---

## How to Use the Code Templates

### Example: Creating a New Controller

1. Copy the template from **QUICK_REFERENCE.md** > "Essential Patterns for PgController"
2. Replace class name, service, and endpoints
3. Follow the exact pattern for:
   - Class annotations (@RestController, @RequestMapping, @Tag, etc.)
   - Method annotations (@GetMapping, @PostMapping, etc.)
   - Response types (ResponseEntity)
   - HTTP status codes

### Example: Creating a New Service

1. Use the template from **QUICK_REFERENCE.md** > "Service Layer Template"
2. Inject repository dependencies via constructor
3. Add @Transactional to methods
4. Use mapper to convert entities to DTOs
5. Throw custom exceptions (don't catch, let GlobalExceptionHandler handle)

### Example: Creating DTOs

1. Use templates from **QUICK_REFERENCE.md** > "DTO Templates"
2. Request DTOs:
   - Use @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
   - Add validation annotations (@NotBlank, @Email, @Pattern, etc.)
3. Response DTOs:
   - Use same Lombok annotations
   - No validation needed (already validated at input)

---

## Reference Materials

### Architecture Components

#### Controllers
- Location: `org.recnos.pg.controller.*`
- Example: `UserController.java`
- Pattern: Receive request, validate, delegate to service, return response

#### Services
- Location: `org.recnos.pg.service.*`
- Pattern: Business logic, transactions, validation, exception handling
- Always use @Transactional, never touch repositories directly in controller

#### Repositories
- Location: `org.recnos.pg.repository.*`
- Pattern: Extend JpaRepository, define custom query methods
- Spring Data auto-generates SQL from method names

#### Entities
- Location: `org.recnos.pg.model.entity.*`
- Pattern: JPA mapped classes with relationships, timestamps, validation
- Example: `Pg.java` - 233 lines with complete field mapping

#### DTOs
- Location: `org.recnos.pg.model.dto.*`
- Request: `/request/` - with validation annotations
- Response: `/response/` - without validation
- Mappers convert between entities and DTOs

#### Mappers
- Location: `org.recnos.pg.mapper.*`
- Pattern: @Component beans with toDTO() methods
- Manual mapping for flexibility

### Security
- JWT Token: JJWT library
- Filters: JwtAuthenticationFilter
- Config: SecurityConfig.java
- Context: SecurityContextHolder utility

### Exception Handling
- Custom exceptions in: `org.recnos.pg.exception.*`
- Global handler: `GlobalExceptionHandler.java`
- Auto HTTP status code mapping

### Validation
- DTO level: Jakarta validation annotations
- Service level: Business rule checks
- Global handler: Catches MethodArgumentNotValidException

---

## Common Patterns Summary

### Constructor Injection
```java
@RestController
@RequiredArgsConstructor  // Auto-generates constructor
public class Controller {
    private final Service service;  // Injected
}
```

### Service with Transaction
```java
@Service
@RequiredArgsConstructor
public class Service {
    @Transactional(readOnly = true)
    public DTO get(UUID id) { }
    
    @Transactional
    public DTO create(CreateRequest req) { }
}
```

### Repository Query
```java
public interface Repository extends JpaRepository<Entity, UUID> {
    Optional<Entity> findById(UUID id);
    Page<Entity> findByCity(String city, Pageable page);
    boolean existsByEmail(String email);
}
```

### Exception Throwing
```java
throw new ResourceNotFoundException("Entity not found");
throw new BadRequestException("Invalid input");
throw new ForbiddenException("Unauthorized access");
throw new DuplicateResourceException("Email exists");
```

### DTO Validation
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    @NotBlank(message = "Name required")
    @Size(min = 3, max = 100)
    private String name;
    
    @Email
    private String email;
}
```

---

## Next Steps

1. **Read DOCUMENTATION_GUIDE.md** (10 min)
   - Understand what documentation exists and how to use it

2. **Skim QUICK_REFERENCE.md** (5 min)
   - Get familiar with code templates and patterns

3. **Review UserController.java** (10 min)
   - See a real implementation of the patterns

4. **Check CODEBASE_STRUCTURE.md** Section 4 (10 min)
   - Understand controller pattern details

5. **Start Implementing** - Use templates from QUICK_REFERENCE.md
   - Copy patterns, adjust names, follow structure

6. **Reference ARCHITECTURE.md** as needed
   - When you need to understand how components work together

---

## Search Tips

To find specific information quickly:

### Controller Questions
→ QUICK_REFERENCE.md "Essential Patterns for PgController" or CODEBASE_STRUCTURE.md Section 4

### Service Questions
→ QUICK_REFERENCE.md "Service Layer Template" or CODEBASE_STRUCTURE.md Section 10

### DTO Questions
→ QUICK_REFERENCE.md "DTO Templates" or CODEBASE_STRUCTURE.md Section 8

### Database Questions
→ CODEBASE_STRUCTURE.md Section 5 or QUICK_REFERENCE.md "Common Operations"

### Security Questions
→ CODEBASE_STRUCTURE.md Section 8 or ARCHITECTURE.md "Security Architecture"

### Error Handling
→ QUICK_REFERENCE.md "Exception Handling" or ARCHITECTURE.md "Exception Handling Flow"

### How Things Connect
→ ARCHITECTURE.md (all diagrams and flows)

---

## Success Criteria

You've successfully understood the codebase when you can:

1. Explain the 4-layer architecture (Controller → Service → Repository → DB)
2. Create a new controller following the UserController pattern
3. Create a new service with proper transactions
4. Create request/response DTOs with validation
5. Understand how JwtAuthenticationFilter secures endpoints
6. Throw appropriate custom exceptions
7. Trace a request through the entire system
8. Understand how errors map to HTTP status codes
9. Write queries using Spring Data repository methods
10. Map entities to DTOs using mappers

---

## Questions?

Refer to the documentation:
1. What exactly are you trying to do? → Find task in "Quick Navigation by Task"
2. Which section? → Go to recommended documentation file
3. Need code example? → Check QUICK_REFERENCE.md first
4. Need deep understanding? → Check CODEBASE_STRUCTURE.md
5. How do components interact? → Check ARCHITECTURE.md

---

## Document Versions

- **DOCUMENTATION_GUIDE.md** - v1.0 (Main reference guide)
- **QUICK_REFERENCE.md** - v1.0 (Code templates)
- **CODEBASE_STRUCTURE.md** - v1.0 (Comprehensive reference)
- **ARCHITECTURE.md** - v1.0 (System architecture)

Last updated: November 16, 2025

---

## Summary

You now have:
1. **80KB+ of comprehensive documentation**
2. **2,350+ lines of detailed explanations**
3. **Code templates ready to copy/paste**
4. **Complete architecture diagrams**
5. **Step-by-step implementation guides**
6. **Pattern reference tables**

Everything you need to implement the PgController and any other features following the project's established patterns.

Happy coding!
