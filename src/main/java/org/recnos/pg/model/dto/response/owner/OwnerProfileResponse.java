package org.recnos.pg.model.dto.response.owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerProfileResponse {
    private UUID id;
    private String email;
    private String mobile;
    private String name;
    private String profilePicture;
    private String companyName;
    private Boolean isEmailVerified;
    private Boolean isMobileVerified;
    private Boolean isVerified;
    private String verificationStatus;
    private Integer trustScore;
    private Instant lastLogin;
    private Instant createdAt;
}
