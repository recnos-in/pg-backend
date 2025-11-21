package org.recnos.pg.controller.owner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.auth.OtpSendRequest;
import org.recnos.pg.model.dto.request.auth.OtpVerificationRequest;
import org.recnos.pg.model.dto.response.auth.OtpResponse;
import org.recnos.pg.model.dto.response.owner.OwnerLoginResponse;
import org.recnos.pg.model.dto.response.owner.OwnerProfileResponse;
import org.recnos.pg.service.auth.AuthService;
import org.recnos.pg.service.auth.OtpService;
import org.recnos.pg.service.owner.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/owner")
@RequiredArgsConstructor
@Tag(name = "Owner Management", description = "APIs for owner authentication and profile management")
public class OwnerController {

    private final OwnerService ownerService;
    private final OtpService otpService;
    private final AuthService authService;

    // ==================== Authentication Endpoints ====================

    @PostMapping("/auth/send-otp")
    @Operation(summary = "Send OTP to mobile number", description = "Send a 6-digit OTP to the owner's mobile number for authentication")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        // Ensure user type is OWNER
        request.setUserType("OWNER");
        OtpResponse response = otpService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/verify-otp")
    @Operation(summary = "Verify OTP and login", description = "Verify OTP and login owner. Creates account if mobile number doesn't exist.")
    public ResponseEntity<OwnerLoginResponse> verifyOtpAndLogin(@Valid @RequestBody OtpVerificationRequest request) {
        // Ensure user type is OWNER
        request.setUserType("OWNER");
        OwnerLoginResponse response = authService.loginOwnerWithOtp(request);
        return ResponseEntity.ok(response);
    }

    // ==================== Profile Management Endpoints ====================

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current owner profile", description = "Get the authenticated owner's profile information")
    public ResponseEntity<OwnerProfileResponse> getCurrentOwner() {
        OwnerProfileResponse profile = ownerService.getCurrentOwnerProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{owner_id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get owner by ID", description = "Get owner profile information by owner ID")
    public ResponseEntity<OwnerProfileResponse> getOwnerById(@PathVariable("owner_id") UUID ownerId) {
        OwnerProfileResponse profile = ownerService.getOwnerProfileById(ownerId);
        return ResponseEntity.ok(profile);
    }
}