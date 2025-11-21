package org.recnos.pg.repository;

import org.recnos.pg.model.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

    Optional<Otp> findByMobileAndUserTypeAndIsVerifiedFalseAndExpiresAtAfter(
            String mobile, String userType, LocalDateTime currentTime);

    void deleteByMobileAndUserType(String mobile, String userType);

    void deleteByExpiresAtBefore(LocalDateTime currentTime);
}