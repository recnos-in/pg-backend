package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "callbacks", schema = "public", indexes = {
        @Index(name = "idx_callbacks_owner_status", columnList = "owner_id, status")
})
public class Callback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pg_id", nullable = false)
    private Pg pg;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_mobile", nullable = false, length = 15)
    private String userMobile;

    @Column(name = "preferred_time", length = 100)
    private String preferredTime;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ColumnDefault("'pending'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "called_at")
    private Instant calledAt;

    @Column(name = "call_notes", length = Integer.MAX_VALUE)
    private String callNotes;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "follow_up_notes", length = Integer.MAX_VALUE)
    private String followUpNotes;

    @ColumnDefault("false")
    @Column(name = "is_converted")
    private Boolean isConverted;

    @Column(name = "converted_at")
    private Instant convertedAt;

    @ColumnDefault("(CURRENT_TIMESTAMP + '7 days')")
    @Column(name = "expires_at")
    private Instant expiresAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}