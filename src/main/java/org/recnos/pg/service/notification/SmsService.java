package org.recnos.pg.service.notification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.recnos.pg.config.TwilioConfig;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final TwilioConfig twilioConfig;

    /**
     * Send OTP via SMS using Twilio
     */
    public void sendOtp(String mobile, String otp) {
        log.info("Sending OTP to mobile: {}", mobile);

        try {
            if (twilioConfig.getAccountSid() == null ||
                twilioConfig.getAccountSid().startsWith("your-")) {
                // Development mode - just log
                log.info("[DEV MODE] OTP for {}: {}", mobile, otp);
                return;
            }

            String messageBody = String.format(
                "Your PG verification code is: %s. Valid for 5 minutes. Do not share this code.",
                otp
            );

            Message message = Message.creator(
                    new PhoneNumber("+91" + mobile),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    messageBody
            ).create();

            log.info("SMS sent successfully. SID: {}", message.getSid());

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", mobile, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP. Please try again.", e);
        }
    }
}