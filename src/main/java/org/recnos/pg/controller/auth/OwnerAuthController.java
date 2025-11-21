package org.recnos.pg.controller.auth;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.auth.OtpVerificationRequest;
import org.recnos.pg.model.dto.request.auth.RegisterRequest;
import org.recnos.pg.model.dto.response.auth.RegisterResponse;
import org.recnos.pg.model.dto.response.owner.OwnerLoginResponse;
import org.recnos.pg.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth/owner")
@RequiredArgsConstructor
@Tag(name = "Owner Authentication", description = "APIs for owner registration and login")
public class OwnerAuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<OwnerLoginResponse> registerOwner(@Valid @RequestBody RegisterRequest registerRequest) {
        OwnerLoginResponse response = authService.registerOwner(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<OwnerLoginResponse> loginOwner(@Valid @RequestBody OtpVerificationRequest otpVerificationRequest) {
        OwnerLoginResponse response = authService.loginOwnerWithOtp(otpVerificationRequest);
        return ResponseEntity.ok(response);
    }
}