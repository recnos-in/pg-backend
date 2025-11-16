package org.recnos.pg.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Getter
@Setter
public class FileUploadProperties {

    private String maxFileSize = "5MB";
    private String maxRequestSize = "25MB";
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "webp", "heic");
    private int maxImagesPerRequest = 10;
    private int maxDimension = 1920;
    private int presignedUrlExpiration = 15; // minutes

    private RateLimit rateLimit = new RateLimit();

    @Getter
    @Setter
    public static class RateLimit {
        private int capacity = 20; // uploads per period
        private int refillTokens = 20;
        private int refillPeriod = 1; // hours
    }

    public long getMaxFileSizeBytes() {
        return parseSizeToBytes(maxFileSize);
    }

    public long getMaxRequestSizeBytes() {
        return parseSizeToBytes(maxRequestSize);
    }

    private long parseSizeToBytes(String size) {
        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        }
        return Long.parseLong(size);
    }
}
