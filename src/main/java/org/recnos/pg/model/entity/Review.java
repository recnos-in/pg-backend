package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reviews", schema = "public", indexes = {
        @Index(name = "idx_reviews_pg", columnList = "pg_id"),
        @Index(name = "idx_reviews_approved", columnList = "is_approved")
}, uniqueConstraints = {
        @UniqueConstraint(name = "reviews_pg_id_user_id_key", columnNames = {"pg_id", "user_id"})
})
public class Review {
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

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_text", length = Integer.MAX_VALUE)
    private String reviewText;

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating;

    @Column(name = "food_rating")
    private Integer foodRating;

    @Column(name = "facilities_rating")
    private Integer facilitiesRating;

    @Column(name = "location_rating")
    private Integer locationRating;

    @Column(name = "value_for_money_rating")
    private Integer valueForMoneyRating;

    @ColumnDefault("false")
    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Admin approvedBy;

    @Column(name = "owner_response", length = Integer.MAX_VALUE)
    private String ownerResponse;

    @Column(name = "owner_responded_at")
    private Instant ownerRespondedAt;

    @ColumnDefault("0")
    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @ColumnDefault("false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}