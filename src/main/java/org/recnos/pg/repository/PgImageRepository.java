package org.recnos.pg.repository;

import org.recnos.pg.model.entity.PgImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PgImageRepository extends JpaRepository<PgImage, UUID> {

    List<PgImage> findByPgIdOrderByDisplayOrderAsc(UUID pgId);

    Optional<PgImage> findByPgIdAndIsPrimaryTrue(UUID pgId);

    void deleteByPgId(UUID pgId);

    void deleteByPgIdAndId(UUID pgId, UUID imageId);
}