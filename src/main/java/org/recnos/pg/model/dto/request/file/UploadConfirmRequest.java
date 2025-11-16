package org.recnos.pg.model.dto.request.file;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadConfirmRequest {

    @NotBlank(message = "S3 key is required")
    @Size(max = 500, message = "S3 key must not exceed 500 characters")
    private String s3Key;

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be at least 1 byte")
    private Long fileSize;

    private String contentType;
}
