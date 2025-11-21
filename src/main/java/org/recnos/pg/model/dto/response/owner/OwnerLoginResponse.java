package org.recnos.pg.model.dto.response.owner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.recnos.pg.model.dto.response.auth.TokenResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerLoginResponse {
    private String message;
    private OwnerProfileResponse owner;
    private TokenResponse tokens;
}
