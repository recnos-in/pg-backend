package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "visits", schema = "public", indexes = {
        @Index(name = "idx_visits_pg", columnList = "pg_id"),
        @Index(name = "idx_visits_user", columnList = "user_id"),
        @Index(name = "idx_visits_owner", columnList = "owner_id"),
        @Index(name = "idx_visits_status", columnList = "status")
})
public class Visit {
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

    @ColumnDefault("'physical'")
    @Column(name = "visit_type", length = 50)
    private String visitType;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "preferred_time_slot", nullable = false, length = 50)
    private String preferredTimeSlot;

    @Column(name = "preferred_time")
    private LocalTime preferredTime;

    @ColumnDefault("'pending'")
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "owner_response", length = 50)
    private String ownerResponse;

    @Column(name = "owner_notes", length = Integer.MAX_VALUE)
    private String ownerNotes;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "rescheduled_date")
    private LocalDate rescheduledDate;

    @Column(name = "rescheduled_time")
    private LocalTime rescheduledTime;

    @Column(name = "rescheduled_reason", length = Integer.MAX_VALUE)
    private String rescheduledReason;

    @Column(name = "cancelled_by", length = 50)
    private String cancelledBy;

    @Column(name = "cancellation_reason", length = Integer.MAX_VALUE)
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completion_notes", length = Integer.MAX_VALUE)
    private String completionNotes;

    @Column(name = "user_notes", length = Integer.MAX_VALUE)
    private String userNotes;

    @Column(name = "special_requirements", length = Integer.MAX_VALUE)
    private String specialRequirements;

    @ColumnDefault("false")
    @Column(name = "whatsapp_reminder_sent")
    private Boolean whatsappReminderSent;

    @Column(name = "whatsapp_reminder_sent_at")
    private Instant whatsappReminderSentAt;

    @ColumnDefault("false")
    @Column(name = "confirmation_sent")
    private Boolean confirmationSent;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}