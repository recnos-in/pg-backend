package org.recnos.pg.model.dto.request.pg;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgUpdateRequest {

    // Basic Information
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    private String summary;

    @Pattern(regexp = "^(Independent house|Apartment|Villa)$", message = "Property type must be Independent house, Apartment, or Villa")
    private String propertyType;

    @Min(value = 1, message = "Total floors must be at least 1")
    private Integer totalFloors;

    @Min(value = 1, message = "Total rooms must be at least 1")
    private Integer totalRooms;

    @Min(value = 1900, message = "Establishment year must be at least 1900")
    @Max(value = 2100, message = "Establishment year must not exceed 2100")
    private Integer establishmentYear;

    // Location
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Size(max = 255, message = "Landmark must not exceed 255 characters")
    private String landmark;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    private Map<String, Object> nearbyLocations;

    // Accommodation
    @Pattern(regexp = "^(Male|Female|Unisex)$", message = "Gender type must be Male, Female, or Unisex")
    private String genderType;

    @Pattern(regexp = "^(Sharing|Private|Both)$", message = "Occupancy type must be Sharing, Private, or Both")
    private String occupancyType;

    @Pattern(regexp = "^(Furnished|Semi-furnished|Unfurnished)$", message = "Furnishing type must be Furnished, Semi-furnished, or Unfurnished")
    private String furnishingType;

    @DecimalMin(value = "0.0", message = "Security deposit must be non-negative")
    private BigDecimal securityDeposit;

    @Min(value = 0, message = "Notice period days must be non-negative")
    private Integer noticePeriodDays;

    // Food
    private Boolean foodAvailable;

    @Pattern(regexp = "^(Veg|Non-veg|Both)$", message = "Food type must be Veg, Non-veg, or Both")
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
    @Pattern(regexp = "^https?://.*", message = "Floor plan URL must be a valid HTTP/HTTPS URL")
    @Size(max = 500, message = "Floor plan URL must not exceed 500 characters")
    private String floorPlanUrl;

    @Pattern(regexp = "^https?://.*", message = "Virtual tour URL must be a valid HTTP/HTTPS URL")
    @Size(max = 500, message = "Virtual tour URL must not exceed 500 characters")
    private String virtualTourUrl;

    // SEO
    @Size(max = 255, message = "Meta title must not exceed 255 characters")
    private String metaTitle;

    private String metaDescription;
}
