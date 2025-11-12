package org.recnos.pg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.recnos.pg")
public class PGApplication {
    public static void main(String[] args) {
        SpringApplication.run(PGApplication.class, args);
    }
}
