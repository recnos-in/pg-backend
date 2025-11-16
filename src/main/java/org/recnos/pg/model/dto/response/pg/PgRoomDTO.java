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
public class PgRoomDTO {
    private UUID id;
    private String roomType;
    private Integer bedsPerRoom;
    private Integer totalRooms;
    private Integer availableBeds;
    private BigDecimal pricePerBed;
    private BigDecimal pricePerMonth;
    private Integer roomSizeSqft;
    private Boolean hasAttachedBathroom;
    private Boolean hasBalcony;
    private Boolean hasAc;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
