package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "owners", schema = "public", indexes = {
        @Index(name = "idx_owners_email", columnList = "email"),
        @Index(name = "idx_owners_mobile", columnList = "mobile")
}, uniqueConstraints = {
        @UniqueConstraint(name = "owners_email_key", columnNames = {"email"}),
        @UniqueConstraint(name = "owners_mobile_key", columnNames = {"mobile"}),
        @UniqueConstraint(name = "owners_google_id_key", columnNames = {"google_id"})
})
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "company_name")
    private String companyName;

    @ColumnDefault("false")
    @Column(name = "is_email_verified")
    private Boolean isEmailVerified;

    @ColumnDefault("false")
    @Column(name = "is_mobile_verified")
    private Boolean isMobileVerified;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_expires")
    private Instant emailVerificationExpires;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private Instant passwordResetExpires;

    @ColumnDefault("false")
    @Column(name = "mfa_enabled")
    private Boolean mfaEnabled;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "google_id")
    private String googleId;

    @ColumnDefault("false")
    @Column(name = "is_verified")
    private Boolean isVerified;

    @ColumnDefault("'pending'")
    @Column(name = "verification_status", length = 50)
    private String verificationStatus;

    @Column(name = "id_proof_url", length = 500)
    private String idProofUrl;

    @Column(name = "address_proof_url", length = 500)
    private String addressProofUrl;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc_code", length = 20)
    private String bankIfscCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "verification_notes", length = Integer.MAX_VALUE)
    private String verificationNotes;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @ColumnDefault("50")
    @Column(name = "trust_score")
    private Integer trustScore;

    @Column(name = "response_time_avg")
    private Integer responseTimeAvg;

    @Column(name = "visit_conversion_rate", precision = 5, scale = 2)
    private BigDecimal visitConversionRate;

    @ColumnDefault("0")
    @Column(name = "complaint_count")
    private Integer complaintCount;

    @ColumnDefault("false")
    @Column(name = "auto_respond_enabled")
    private Boolean autoRespondEnabled;

    @Column(name = "auto_respond_message", length = Integer.MAX_VALUE)
    private String autoRespondMessage;

    @Column(name = "availability_hours")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> availabilityHours;

    @Column(name = "notification_preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> notificationPreferences;

    @Column(name = "last_login")
    private Instant lastLogin;

    @ColumnDefault("0")
    @Column(name = "login_attempts")
    private Integer loginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @ColumnDefault("false")
    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @Column(name = "blocked_reason", length = Integer.MAX_VALUE)
    private String blockedReason;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by")
    private User blockedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}