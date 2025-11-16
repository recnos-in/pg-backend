package org.recnos.pg.repository;

import org.recnos.pg.model.entity.PgRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PgRoomRepository extends JpaRepository<PgRoom, UUID> {

    List<PgRoom> findByPgId(UUID pgId);

    void deleteByPgId(UUID pgId);

    @Query("SELECT MIN(r.pricePerMonth) FROM PgRoom r WHERE r.pg.id = :pgId")
    BigDecimal findMinPriceByPgId(@Param("pgId") UUID pgId);

    @Query("SELECT MAX(r.pricePerMonth) FROM PgRoom r WHERE r.pg.id = :pgId")
    BigDecimal findMaxPriceByPgId(@Param("pgId") UUID pgId);

    @Query("SELECT SUM(r.availableBeds) FROM PgRoom r WHERE r.pg.id = :pgId")
    Integer getTotalAvailableBedsByPgId(@Param("pgId") UUID pgId);
}