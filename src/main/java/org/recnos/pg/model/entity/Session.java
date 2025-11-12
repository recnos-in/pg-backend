package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sessions", schema = "public", indexes = {
        @Index(name = "idx_sessions_user", columnList = "user_id, user_type"),
        @Index(name = "idx_sessions_token", columnList = "token")
}, uniqueConstraints = {
        @UniqueConstraint(name = "sessions_token_key", columnNames = {"token"}),
        @UniqueConstraint(name = "sessions_refresh_token_key", columnNames = {"refresh_token"})
})
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = Integer.MAX_VALUE)
    private String userAgent;

    @Column(name = "device_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> deviceInfo;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

}