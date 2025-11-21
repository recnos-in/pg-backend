package org.recnos.pg.model.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.recnos.pg.model.dto.response.user.UserProfileResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private UserProfileResponse user;
    private TokenResponse tokens;
}