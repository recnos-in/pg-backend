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
@Table(name = "popular_cities", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "popular_cities_city_name_key", columnNames = {"city_name"})
})
public class PopularCity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ColumnDefault("0")
    @Column(name = "listing_count")
    private Integer listingCount;

    @ColumnDefault("0")
    @Column(name = "display_order")
    private Integer displayOrder;

    @ColumnDefault("true")
    @Column(name = "is_featured")
    private Boolean isFeatured;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}