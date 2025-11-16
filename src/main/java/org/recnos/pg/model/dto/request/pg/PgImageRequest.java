package org.recnos.pg.model.dto.request.pg;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgImageRequest {

    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^https?://.*", message = "Image URL must be a valid HTTP/HTTPS URL")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Pattern(regexp = "^https?://.*", message = "Thumbnail URL must be a valid HTTP/HTTPS URL")
    @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters")
    private String thumbnailUrl;

    @Pattern(regexp = "^(exterior|room|kitchen|bathroom|common_area|other)$",
             message = "Image type must be exterior, room, kitchen, bathroom, common_area, or other")
    private String imageType;

    @Min(value = 0, message = "Display order must be non-negative")
    private Integer displayOrder;

    private Boolean isPrimary;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;
}
