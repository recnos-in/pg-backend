package org.recnos.pg.model.dto.response.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgDetailResponse {

    private UUID id;
    private UUID ownerId;
    private String ownerName;

    // Basic Information
    private String name;
    private String slug;
    private String description;
    private String summary;
    private String propertyType;
    private Integer totalFloors;
    private Integer totalRooms;
    private Integer establishmentYear;

    // Location
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String landmark;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Map<String, Object> nearbyLocations;

    // Accommodation
    private String genderType;
    private String occupancyType;
    private String furnishingType;
    private BigDecimal securityDeposit;
    private Integer noticePeriodDays;

    // Food
    private Boolean foodAvailable;
    private String foodType;
    private Map<String, Object> foodPlans;
    private String foodDescription;
    private Map<String, Object> foodPricing;

    // Rules & Policies
    private String houseRules;
    private String cancellationPolicy;
    private String paymentTerms;
    private LocalTime curfewTime;
    private String guestPolicy;

    // Media
    private String floorPlanUrl;
    private String virtualTourUrl;

    // Status
    private String status;
    private String approvalStatus;
    private String approvalNotes;
    private Instant approvedAt;
    private String approvedByName;
    private String rejectedReason;

    // Featured
    private Boolean isFeatured;
    private Instant featuredUntil;
    private List<String> featuredLocations;

    // Metrics
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer contactClickCount;
    private Integer visitRequestCount;
    private Integer shareCount;

    // SEO
    private String metaTitle;
    private String metaDescription;

    // Nested Data
    private List<PgRoomDTO> rooms;
    private List<PgImageDTO> images;
    private List<AmenityDTO> amenities;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
