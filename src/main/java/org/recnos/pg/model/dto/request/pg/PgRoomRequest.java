package org.recnos.pg.model.dto.request.pg;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgRoomRequest {

    @NotBlank(message = "Room type is required")
    @Pattern(regexp = "^(Single|Double|Triple|Dormitory)$", message = "Room type must be Single, Double, Triple, or Dormitory")
    private String roomType;

    @NotNull(message = "Beds per room is required")
    @Min(value = 1, message = "Beds per room must be at least 1")
    private Integer bedsPerRoom;

    @NotNull(message = "Total rooms is required")
    @Min(value = 1, message = "Total rooms must be at least 1")
    private Integer totalRooms;

    @NotNull(message = "Available beds is required")
    @Min(value = 0, message = "Available beds must be non-negative")
    private Integer availableBeds;

    @NotNull(message = "Price per bed is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per bed must be greater than 0")
    private BigDecimal pricePerBed;

    @NotNull(message = "Price per month is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per month must be greater than 0")
    private BigDecimal pricePerMonth;

    @Min(value = 1, message = "Room size must be at least 1 sqft")
    private Integer roomSizeSqft;

    private Boolean hasAttachedBathroom;

    private Boolean hasBalcony;

    private Boolean hasAc;

    private String description;
}
