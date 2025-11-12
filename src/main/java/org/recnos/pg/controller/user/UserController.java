package org.recnos.pg.controller.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.model.dto.request.user.PasswordChangeRequest;
import org.recnos.pg.model.dto.request.user.PreferencesUpdateRequest;
import org.recnos.pg.model.dto.request.user.UserUpdateRequest;
import org.recnos.pg.model.dto.response.user.UserProfileResponse;
import org.recnos.pg.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user profiles and preferences")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable("user_id") UUID userId) {
        UserProfileResponse profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/{user_id}/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PutMapping("/{user_id}/preferences")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody PreferencesUpdateRequest request) {
        UserProfileResponse updatedProfile = userService.updatePreferences(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
