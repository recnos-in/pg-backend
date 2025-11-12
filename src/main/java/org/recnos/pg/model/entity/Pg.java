package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.recnos.pg.model.enums.GenderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pgs", schema = "public", indexes = {
        @Index(name = "idx_pgs_owner", columnList = "owner_id"),
        @Index(name = "idx_pgs_slug", columnList = "slug"),
        @Index(name = "idx_pgs_city_status", columnList = "city, status"),
        @Index(name = "idx_pgs_city", columnList = "city"),
        @Index(name = "idx_pgs_location", columnList = "location"),
        @Index(name = "idx_pgs_gender_type", columnList = "gender_type"),
        @Index(name = "idx_pgs_status", columnList = "status"),
        @Index(name = "idx_pgs_is_featured", columnList = "is_featured")
}, uniqueConstraints = {
        @UniqueConstraint(name = "pgs_slug_key", columnNames = {"slug"})
})
public class Pg {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "summary", length = Integer.MAX_VALUE)
    private String summary;

    @Column(name = "property_type", length = 50)
    private String propertyType;

    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "establishment_year")
    private Integer establishmentYear;

    @Column(name = "address", nullable = false, length = Integer.MAX_VALUE)
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "nearby_locations")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> nearbyLocations;

    @Column(name = "gender_type", length = 20)
    private String genderType;

    @Column(name = "occupancy_type", length = 50)
    private String occupancyType;

    @Column(name = "furnishing_type", length = 50)
    private String furnishingType;

    @Column(name = "security_deposit", precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @ColumnDefault("false")
    @Column(name = "food_available")
    private Boolean foodAvailable;

    @Column(name = "food_type", length = 50)
    private String foodType;

    @Column(name = "food_plans")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> foodPlans;

    @Column(name = "food_description", length = Integer.MAX_VALUE)
    private String foodDescription;

    @Column(name = "food_pricing")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> foodPricing;

    @Column(name = "house_rules", length = Integer.MAX_VALUE)
    private String houseRules;

    @Column(name = "cancellation_policy", length = Integer.MAX_VALUE)
    private String cancellationPolicy;

    @Column(name = "payment_terms", length = Integer.MAX_VALUE)
    private String paymentTerms;

    @Column(name = "curfew_time")
    private LocalTime curfewTime;

    @Column(name = "guest_policy", length = Integer.MAX_VALUE)
    private String guestPolicy;

    @Column(name = "floor_plan_url", length = 500)
    private String floorPlanUrl;

    @Column(name = "virtual_tour_url", length = 500)
    private String virtualTourUrl;

    @ColumnDefault("'draft'")
    @Column(name = "status", length = 50)
    private String status;

    @ColumnDefault("'pending'")
    @Column(name = "approval_status", length = 50)
    private String approvalStatus;

    @Column(name = "approval_notes", length = Integer.MAX_VALUE)
    private String approvalNotes;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Admin approvedBy;

    @Column(name = "rejected_reason", length = Integer.MAX_VALUE)
    private String rejectedReason;

    @ColumnDefault("false")
    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "featured_until")
    private Instant featuredUntil;

    @Column(name = "featured_locations")
    private List<String> featuredLocations;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(name = "favorite_count")
    private Integer favoriteCount;

    @ColumnDefault("0")
    @Column(name = "contact_click_count")
    private Integer contactClickCount;

    @ColumnDefault("0")
    @Column(name = "visit_request_count")
    private Integer visitRequestCount;

    @ColumnDefault("0")
    @Column(name = "share_count")
    private Integer shareCount;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = Integer.MAX_VALUE)
    private String metaDescription;

    @ColumnDefault("false")
    @Column(name = "is_archived")
    private Boolean isArchived;

    @Column(name = "archived_at")
    private Instant archivedAt;

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

/*
 TODO [Reverse Engineering] create field to map the 'location' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "location", columnDefinition = "geography")
    private Object location;
*/
}