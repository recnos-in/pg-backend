package org.recnos.pg.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.recnos.pg.exception.BadRequestException;
import org.recnos.pg.model.dto.request.auth.OtpSendRequest;
import org.recnos.pg.model.dto.request.auth.OtpVerificationRequest;
import org.recnos.pg.model.dto.response.auth.OtpResponse;
import org.recnos.pg.model.entity.Otp;
import org.recnos.pg.repository.OtpRepository;
import org.recnos.pg.service.notification.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;
    private final SmsService smsService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    @Transactional
    public OtpResponse sendOtp(OtpSendRequest request) {
        // Delete any existing OTP for this mobile and user type
        otpRepository.deleteByMobileAndUserType(request.getMobile(), request.getUserType());

        // Generate 6-digit OTP
        String otpCode = generateOtp();

        // Create OTP entity
        Otp otp = Otp.builder()
                .mobile(request.getMobile())
                .otpCode(otpCode)
                .userType(request.getUserType())
                .isVerified(false)
                .attempts(0)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRepository.save(otp);

        // Send OTP via SMS
        smsService.sendOtp(request.getMobile(), otpCode);

        return OtpResponse.builder()
                .message("OTP sent successfully " + otpCode)
                .mobile(request.getMobile())
                .expiresInSeconds(OTP_EXPIRY_MINUTES * 60)
                .build();
    }

    @Transactional
    public boolean verifyOtp(OtpVerificationRequest request) {
        // Find valid OTP
        Otp otp = otpRepository.findByMobileAndUserTypeAndIsVerifiedFalseAndExpiresAtAfter(
                        request.getMobile(),
                        request.getUserType(),
                        LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        // Check attempts
        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            otpRepository.delete(otp);
            throw new BadRequestException("Maximum OTP attempts exceeded. Please request a new OTP");
        }

        // Increment attempts
        otp.setAttempts(otp.getAttempts() + 1);
        otpRepository.save(otp);

        // Verify OTP
        if (!otp.getOtpCode().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        // Mark as verified
        otp.setIsVerified(true);
        otpRepository.save(otp);

        return true;
    }

    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}