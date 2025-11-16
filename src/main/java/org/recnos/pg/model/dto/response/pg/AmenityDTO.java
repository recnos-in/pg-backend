package org.recnos.pg.model.dto.response.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityDTO {
    private UUID id;
    private String name;
    private String category;
    private String iconName;
    private Boolean isPaid;
    private BigDecimal price;
    private String notes;
}
