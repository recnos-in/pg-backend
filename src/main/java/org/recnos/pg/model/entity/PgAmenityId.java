package org.recnos.pg.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class PgAmenityId implements Serializable {
    private static final long serialVersionUID = -5884758676801134141L;
    @Column(name = "pg_id", nullable = false)
    private UUID pgId;

    @Column(name = "amenity_id", nullable = false)
    private UUID amenityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PgAmenityId entity = (PgAmenityId) o;
        return Objects.equals(this.amenityId, entity.amenityId) &&
                Objects.equals(this.pgId, entity.pgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amenityId, pgId);
    }

}