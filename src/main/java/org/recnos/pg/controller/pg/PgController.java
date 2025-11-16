package org.recnos.pg.controller.pg;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.pg.PgCreateRequest;
import org.recnos.pg.model.dto.request.pg.PgUpdateRequest;
import org.recnos.pg.model.dto.response.pg.PgDetailResponse;
import org.recnos.pg.model.dto.response.pg.PgListResponse;
import org.recnos.pg.service.pg.PgService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/pgs")
@RequiredArgsConstructor
@Tag(name = "PG Management", description = "APIs for managing Paying Guest accommodations")
@SecurityRequirement(name = "bearerAuth")
public class PgController {

    private final PgService pgService;

    @PostMapping
    @Operation(summary = "Create a new PG", description = "Create a new Paying Guest accommodation listing")
    public ResponseEntity<PgDetailResponse> createPg(@Valid @RequestBody PgCreateRequest request) {
        PgDetailResponse response = pgService.createPg(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{pg_id}")
    @Operation(summary = "Get PG by ID", description = "Retrieve detailed information about a PG by its ID")
    public ResponseEntity<PgDetailResponse> getPgById(
            @Parameter(description = "PG ID") @PathVariable("pg_id") UUID pgId) {
        PgDetailResponse response = pgService.getPgById(pgId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get PG by slug", description = "Retrieve detailed information about a PG by its slug. This endpoint also increments the view count.")
    public ResponseEntity<PgDetailResponse> getPgBySlug(
            @Parameter(description = "PG slug") @PathVariable String slug) {
        PgDetailResponse response = pgService.getPgBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{pg_id}")
    @Operation(summary = "Update PG", description = "Update an existing PG listing")
    public ResponseEntity<PgDetailResponse> updatePg(
            @Parameter(description = "PG ID") @PathVariable("pg_id") UUID pgId,
            @Valid @RequestBody PgUpdateRequest request) {
        PgDetailResponse response = pgService.updatePg(pgId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{pg_id}")
    @Operation(summary = "Delete PG", description = "Soft delete a PG listing (marks as deleted but doesn't remove from database)")
    public ResponseEntity<Map<String, String>> deletePg(
            @Parameter(description = "PG ID") @PathVariable("pg_id") UUID pgId) {
        pgService.deletePg(pgId);
        return ResponseEntity.ok(Map.of("message", "PG deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all PGs", description = "Retrieve a paginated list of all PG listings")
    public ResponseEntity<Page<PgListResponse>> getAllPgs(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PgListResponse> response = pgService.getAllPgs(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{owner_id}")
    @Operation(summary = "Get PGs by owner", description = "Retrieve all PG listings for a specific owner")
    public ResponseEntity<Page<PgListResponse>> getPgsByOwner(
            @Parameter(description = "Owner ID") @PathVariable("owner_id") UUID ownerId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PgListResponse> response = pgService.getPgsByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get PGs by city", description = "Retrieve all PG listings in a specific city")
    public ResponseEntity<Page<PgListResponse>> getPgsByCity(
            @Parameter(description = "City name") @PathVariable String city,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PgListResponse> response = pgService.getPgsByCity(city, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search PGs", description = "Search for PG listings by keyword in name, description, city, or address")
    public ResponseEntity<Page<PgListResponse>> searchPgs(
            @Parameter(description = "Search query") @RequestParam String query,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PgListResponse> response = pgService.searchPgs(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter PGs", description = "Filter PG listings by multiple criteria")
    public ResponseEntity<Page<PgListResponse>> filterPgs(
            @Parameter(description = "City") @RequestParam(required = false) String city,
            @Parameter(description = "State") @RequestParam(required = false) String state,
            @Parameter(description = "Gender type (Male/Female/Unisex)") @RequestParam(required = false) String genderType,
            @Parameter(description = "Occupancy type (Sharing/Private/Both)") @RequestParam(required = false) String occupancyType,
            @Parameter(description = "Furnishing type (Furnished/Semi-furnished/Unfurnished)") @RequestParam(required = false) String furnishingType,
            @Parameter(description = "Food available") @RequestParam(required = false) Boolean foodAvailable,
            @Parameter(description = "Status") @RequestParam(required = false) String status,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PgListResponse> response = pgService.filterPgs(
                city, state, genderType, occupancyType, furnishingType, foodAvailable, status, pageable);
        return ResponseEntity.ok(response);
    }
}