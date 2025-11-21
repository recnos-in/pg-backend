package org.recnos.pg.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    /**
     * Send OTP via SMS
     * This is a placeholder implementation. Integrate with actual SMS provider like:
     * - Twilio
     * - AWS SNS
     * - MSG91
     * - Firebase
     */
    public void sendOtp(String mobile, String otp) {
        log.info("Sending OTP {} to mobile: {}", otp, mobile);

        // TODO: Replace with actual SMS provider integration
        // Example for Twilio:
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message message = Message.creator(
        //     new PhoneNumber(mobile),
        //     new PhoneNumber(FROM_NUMBER),
        //     "Your OTP is: " + otp
        // ).create();

        // For development, just log the OTP
        log.info("OTP sent successfully: {}", otp);
    }
}