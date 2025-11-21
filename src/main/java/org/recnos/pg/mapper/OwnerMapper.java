package org.recnos.pg.mapper;

import org.recnos.pg.model.dto.response.owner.OwnerProfileResponse;
import org.recnos.pg.model.entity.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {

    public OwnerProfileResponse toProfileResponse(Owner owner) {
        if (owner == null) {
            return null;
        }

        return OwnerProfileResponse.builder()
                .id(owner.getId())
                .email(owner.getEmail())
                .mobile(owner.getMobile())
                .name(owner.getName())
                .profilePicture(owner.getProfilePicture())
                .companyName(owner.getCompanyName())
                .isEmailVerified(owner.getIsEmailVerified())
                .isMobileVerified(owner.getIsMobileVerified())
                .isVerified(owner.getIsVerified())
                .verificationStatus(owner.getVerificationStatus())
                .trustScore(owner.getTrustScore())
                .lastLogin(owner.getLastLogin())
                .createdAt(owner.getCreatedAt())
                .build();
    }
}