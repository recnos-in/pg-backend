package org.recnos.pg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret = "your-secret-key-change-this-in-production-make-it-at-least-256-bits-long";
    private Long accessTokenExpiration = 86400000L; // 24 hours in milliseconds
    private Long refreshTokenExpiration = 604800000L; // 7 days in milliseconds
}