package org.recnos.pg.model.dto.response.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgListResponse {

    private UUID id;
    private String name;
    private String slug;
    private String summary;
    private String propertyType;

    // Location
    private String city;
    private String state;
    private String landmark;

    // Accommodation
    private String genderType;
    private String occupancyType;
    private String furnishingType;

    // Pricing (minimum price from rooms)
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Primary image
    private String primaryImageUrl;
    private String primaryThumbnailUrl;

    // Status
    private String status;
    private String approvalStatus;
    private Boolean isFeatured;

    // Metrics
    private Integer viewCount;
    private Integer favoriteCount;

    // Quick Info
    private Boolean foodAvailable;
    private Integer totalRooms;
    private Integer availableBeds;

    // Timestamps
    private Instant createdAt;
}