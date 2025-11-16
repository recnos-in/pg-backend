package org.recnos.pg.model.dto.response.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiImageUploadResponse {

    private int totalUploaded; // Number of successfully uploaded images
    private int totalFailed; // Number of failed uploads
    private List<ImageUploadResponse> successfulUploads; // List of successful uploads
    private List<UploadError> failedUploads; // List of failed uploads with error details

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadError {
        private String fileName;
        private String error;
        private String errorCode;
    }
}
