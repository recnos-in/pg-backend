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
@Table(name = "admins", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "admins_email_key", columnNames = {"email"})
})
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("'admin'")
    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "permissions")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> permissions;

    @Column(name = "last_login")
    private Instant lastLogin;

    @ColumnDefault("0")
    @Column(name = "login_attempts")
    private Integer loginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}