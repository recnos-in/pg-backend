package org.recnos.pg.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "openai")
@Getter
@Setter
public class OpenAIConfig {

    private String apiKey;
    private String model;
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}