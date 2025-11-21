package org.recnos.pg.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.auth.OtpSendRequest;
import org.recnos.pg.model.dto.request.auth.OtpVerificationRequest;
import org.recnos.pg.model.dto.request.user.PasswordChangeRequest;
import org.recnos.pg.model.dto.request.user.PreferencesUpdateRequest;
import org.recnos.pg.model.dto.request.user.UserUpdateRequest;
import org.recnos.pg.model.dto.response.auth.LoginResponse;
import org.recnos.pg.model.dto.response.auth.OtpResponse;
import org.recnos.pg.model.dto.response.user.UserProfileResponse;
import org.recnos.pg.service.auth.AuthService;
import org.recnos.pg.service.auth.OtpService;
import org.recnos.pg.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user authentication and profile management")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final AuthService authService;

    // ==================== Authentication Endpoints ====================

    @PostMapping("/auth/send-otp")
    @Operation(summary = "Send OTP to mobile number", description = "Send a 6-digit OTP to the user's mobile number for authentication")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        // Ensure user type is USER
        request.setUserType("USER");
        OtpResponse response = otpService.sendOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/verify-otp")
    @Operation(summary = "Verify OTP and login", description = "Verify OTP and login user. Creates account if mobile number doesn't exist.")
    public ResponseEntity<LoginResponse> verifyOtpAndLogin(@Valid @RequestBody OtpVerificationRequest request) {
        // Ensure user type is USER
        request.setUserType("USER");
        LoginResponse response = authService.loginWithOtp(request);
        return ResponseEntity.ok(response);
    }

    // ==================== Profile Management Endpoints ====================

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user profile", description = "Get the authenticated user's profile information")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{user_id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user by ID", description = "Get user profile information by user ID")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable("user_id") UUID userId) {
        UserProfileResponse profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{user_id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/{user_id}/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping("/{user_id}/preferences")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user preferences", description = "Update user preferences for PG search")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PreferencesUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updatePreferences(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
