package org.recnos.pg.service.user;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.exception.BadRequestException;
import org.recnos.pg.exception.DuplicateResourceException;
import org.recnos.pg.exception.ForbiddenException;
import org.recnos.pg.exception.ResourceNotFoundException;
import org.recnos.pg.mapper.UserMapper;
import org.recnos.pg.model.dto.request.user.PasswordChangeRequest;
import org.recnos.pg.model.dto.request.user.PreferencesUpdateRequest;
import org.recnos.pg.model.dto.request.user.UserUpdateRequest;
import org.recnos.pg.model.dto.response.user.UserProfileResponse;
import org.recnos.pg.model.entity.User;
import org.recnos.pg.repository.UserRepository;
import org.recnos.pg.security.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        UUID userId = SecurityContextHolder.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userMapper.toProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(UUID userId) {
        User user = findById(userId);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateUser(UUID userId, UserUpdateRequest request) {
        UUID currentUserId = SecurityContextHolder.getCurrentUserId();

        // Only allow users to update their own profile
        if (!currentUserId.equals(userId)) {
            throw new ForbiddenException("You can only update your own profile");
        }

        User user = findById(userId);

        // Check for duplicate email
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Check for duplicate mobile
        if (request.getMobile() != null && !request.getMobile().equals(user.getMobile())) {
            if (userRepository.existsByMobile(request.getMobile())) {
                throw new DuplicateResourceException("Mobile number already exists");
            }
            user.setMobile(request.getMobile());
            user.setIsMobileVerified(false); // Reset verification when mobile changes
        }

        // Update other fields
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getOccupation() != null) {
            user.setOccupation(request.getOccupation());
        }
        if (request.getPreferredLocations() != null) {
            user.setPreferredLocations(request.getPreferredLocations());
        }
        if (request.getBudgetMin() != null) {
            user.setBudgetMin(request.getBudgetMin());
        }
        if (request.getBudgetMax() != null) {
            user.setBudgetMax(request.getBudgetMax());
        }
        if (request.getMoveInDate() != null) {
            user.setMoveInDate(request.getMoveInDate());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toProfileResponse(updatedUser);
    }

    @Transactional
    public void changePassword(UUID userId, PasswordChangeRequest request) {
        UUID currentUserId = SecurityContextHolder.getCurrentUserId();

        // Only allow users to change their own password
        if (!currentUserId.equals(userId)) {
            throw new ForbiddenException("You can only change your own password");
        }

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        User user = findById(userId);

        // Verify current password
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse updatePreferences(UUID userId, PreferencesUpdateRequest request) {
        UUID currentUserId = SecurityContextHolder.getCurrentUserId();

        // Only allow users to update their own preferences
        if (!currentUserId.equals(userId)) {
            throw new ForbiddenException("You can only update your own preferences");
        }

        User user = findById(userId);

        if (request.getPreferredLocations() != null) {
            user.setPreferredLocations(request.getPreferredLocations());
        }
        if (request.getBudgetMin() != null) {
            user.setBudgetMin(request.getBudgetMin());
        }
        if (request.getBudgetMax() != null) {
            user.setBudgetMax(request.getBudgetMax());
        }
        if (request.getMoveInDate() != null) {
            user.setMoveInDate(request.getMoveInDate());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toProfileResponse(updatedUser);
    }
}
