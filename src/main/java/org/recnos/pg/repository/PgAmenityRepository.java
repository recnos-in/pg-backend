package org.recnos.pg.repository;

import org.recnos.pg.model.entity.PgAmenity;
import org.recnos.pg.model.entity.PgAmenityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PgAmenityRepository extends JpaRepository<PgAmenity, PgAmenityId> {

    List<PgAmenity> findByPgId(UUID pgId);

    void deleteByPgId(UUID pgId);

    void deleteByPgIdAndAmenityId(UUID pgId, UUID amenityId);
}