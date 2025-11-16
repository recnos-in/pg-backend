package org.recnos.pg.model.dto.response.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgImageDTO {
    private UUID id;
    private String imageUrl;
    private String thumbnailUrl;
    private String imageType;
    private Integer displayOrder;
    private Boolean isPrimary;
    private String altText;
    private Instant uploadedAt;
}
