package org.recnos.pg.mapper;

import org.recnos.pg.model.dto.response.user.UserProfileResponse;
import org.recnos.pg.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .gender(user.getGender())
                .occupation(user.getOccupation())
                .preferredLocations(user.getPreferredLocations())
                .budgetMin(user.getBudgetMin())
                .budgetMax(user.getBudgetMax())
                .moveInDate(user.getMoveInDate())
                .isEmailVerified(user.getIsEmailVerified())
                .isMobileVerified(user.getIsMobileVerified())
                .mfaEnabled(user.getMfaEnabled())
                .googleId(user.getGoogleId())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
