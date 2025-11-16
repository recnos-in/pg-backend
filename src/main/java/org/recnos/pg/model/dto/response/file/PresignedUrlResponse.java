package org.recnos.pg.model.dto.response.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {

    private String uploadUrl; // Presigned URL for client to upload to
    private String s3Key; // S3 object key that will be used
    private String fileName; // Filename that will be used
    private Instant expiresAt; // When the presigned URL expires
    private Integer expiresInMinutes; // Expiration time in minutes
    private String method; // HTTP method to use (usually PUT)
    private String contentType; // Content type to set in upload request
}
