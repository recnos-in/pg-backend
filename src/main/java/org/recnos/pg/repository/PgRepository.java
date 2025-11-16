package org.recnos.pg.repository;

import org.recnos.pg.model.entity.Pg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PgRepository extends JpaRepository<Pg, UUID> {

    Optional<Pg> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Pg> findByOwnerId(UUID ownerId);

    Page<Pg> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Pg> findByCity(String city, Pageable pageable);

    Page<Pg> findByCityAndStatus(String city, String status, Pageable pageable);

    Page<Pg> findByStatus(String status, Pageable pageable);

    Page<Pg> findByApprovalStatus(String approvalStatus, Pageable pageable);

    Page<Pg> findByIsFeatured(Boolean isFeatured, Pageable pageable);

    Page<Pg> findByGenderType(String genderType, Pageable pageable);

    @Query("SELECT p FROM Pg p WHERE p.city = :city AND p.genderType = :genderType AND p.status = 'approved'")
    Page<Pg> findByCityAndGenderType(@Param("city") String city, @Param("genderType") String genderType, Pageable pageable);

    @Query("SELECT p FROM Pg p WHERE " +
           "(:city IS NULL OR p.city = :city) AND " +
           "(:state IS NULL OR p.state = :state) AND " +
           "(:genderType IS NULL OR p.genderType = :genderType) AND " +
           "(:occupancyType IS NULL OR p.occupancyType = :occupancyType) AND " +
           "(:furnishingType IS NULL OR p.furnishingType = :furnishingType) AND " +
           "(:foodAvailable IS NULL OR p.foodAvailable = :foodAvailable) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Pg> findByFilters(
            @Param("city") String city,
            @Param("state") String state,
            @Param("genderType") String genderType,
            @Param("occupancyType") String occupancyType,
            @Param("furnishingType") String furnishingType,
            @Param("foodAvailable") Boolean foodAvailable,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT p FROM Pg p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Pg> searchPgs(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pg p WHERE p.owner.id = :ownerId")
    Long countByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT COUNT(p) FROM Pg p WHERE p.city = :city AND p.status = 'approved'")
    Long countByCityAndApproved(@Param("city") String city);
}