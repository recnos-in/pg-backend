package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_preferences", schema = "public")
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

    @ColumnDefault("true")
    @Column(name = "email_enabled")
    private Boolean emailEnabled;

    @ColumnDefault("true")
    @Column(name = "sms_enabled")
    private Boolean smsEnabled;

    @ColumnDefault("true")
    @Column(name = "whatsapp_enabled")
    private Boolean whatsappEnabled;

    @ColumnDefault("true")
    @Column(name = "push_enabled")
    private Boolean pushEnabled;

    @ColumnDefault("true")
    @Column(name = "in_app_enabled")
    private Boolean inAppEnabled;

    @ColumnDefault("true")
    @Column(name = "visit_notifications")
    private Boolean visitNotifications;

    @ColumnDefault("true")
    @Column(name = "payment_notifications")
    private Boolean paymentNotifications;

    @ColumnDefault("false")
    @Column(name = "marketing_notifications")
    private Boolean marketingNotifications;

    @ColumnDefault("true")
    @Column(name = "weekly_digest")
    private Boolean weeklyDigest;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}