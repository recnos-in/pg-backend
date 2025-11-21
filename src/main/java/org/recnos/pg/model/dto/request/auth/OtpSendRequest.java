package org.recnos.pg.model.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpSendRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid mobile number format")
    private String mobile;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(USER|OWNER)$", message = "User type must be USER or OWNER")
    private String userType; // "USER" or "OWNER"
}