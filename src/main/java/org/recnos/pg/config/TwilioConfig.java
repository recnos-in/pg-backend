package org.recnos.pg.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
@Slf4j
public class TwilioConfig {

    private String accountSid;
    private String authToken;
    private String phoneNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.startsWith("your-")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully");
        } else {
            log.warn("Twilio credentials not configured. SMS will be logged only.");
        }
    }
}