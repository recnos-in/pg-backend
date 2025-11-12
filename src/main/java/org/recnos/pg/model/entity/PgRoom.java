package org.recnos.pg.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pg_rooms", schema = "public", indexes = {
        @Index(name = "idx_pg_rooms_pg", columnList = "pg_id"),
        @Index(name = "idx_pg_rooms_type_price", columnList = "room_type, price_per_month")
})
public class PgRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pg_id", nullable = false)
    private Pg pg;

    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType;

    @Column(name = "beds_per_room", nullable = false)
    private Integer bedsPerRoom;

    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;

    @Column(name = "available_beds", nullable = false)
    private Integer availableBeds;

    @Column(name = "price_per_bed", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerBed;

    @Column(name = "price_per_month", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMonth;

    @Column(name = "room_size_sqft")
    private Integer roomSizeSqft;

    @ColumnDefault("false")
    @Column(name = "has_attached_bathroom")
    private Boolean hasAttachedBathroom;

    @ColumnDefault("false")
    @Column(name = "has_balcony")
    private Boolean hasBalcony;

    @ColumnDefault("false")
    @Column(name = "has_ac")
    private Boolean hasAc;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}