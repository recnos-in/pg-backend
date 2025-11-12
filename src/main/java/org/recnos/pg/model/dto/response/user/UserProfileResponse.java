package org.recnos.pg.model.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String email;
    private String mobile;
    private String name;
    private String profilePicture;
    private String gender;
    private String occupation;
    private List<String> preferredLocations;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private LocalDate moveInDate;
    private Boolean isEmailVerified;
    private Boolean isMobileVerified;
    private Boolean mfaEnabled;
    private String googleId;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
