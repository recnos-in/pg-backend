package org.recnos.pg.model.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotBlank(message = "Content type is required")
    @Pattern(regexp = "^image/(jpeg|png|webp|heic)$", message = "Content type must be image/jpeg, image/png, image/webp, or image/heic")
    private String contentType;

    @NotBlank(message = "Folder is required")
    @Pattern(regexp = "^[a-zA-Z0-9/_-]+$", message = "Folder must contain only alphanumeric characters, slashes, underscores, and hyphens")
    private String folder; // e.g., "pgs", "profiles", "documents"

    @Pattern(regexp = "^(pg|profile|document|other)$", message = "Context type must be pg, profile, document, or other")
    private String contextType; // Optional: to categorize the image
}
