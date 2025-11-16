package org.recnos.pg.model.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {

    @NotBlank(message = "Folder is required")
    @Pattern(regexp = "^[a-zA-Z0-9/_-]+$", message = "Folder must contain only alphanumeric characters, slashes, underscores, and hyphens")
    private String folder; // e.g., "pgs", "profiles", "documents"

    @Pattern(regexp = "^(pg|profile|document|other)$", message = "Context type must be pg, profile, document, or other")
    private String contextType; // Optional: to categorize the image

    private String description; // Optional description for the image
}
