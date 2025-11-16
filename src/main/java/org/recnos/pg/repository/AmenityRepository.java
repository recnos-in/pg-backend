package org.recnos.pg.repository;

import org.recnos.pg.model.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {

    Optional<Amenity> findByName(String name);

    List<Amenity> findByIsActiveTrue();

    List<Amenity> findByCategory(String category);

    List<Amenity> findByIsActiveTrueOrderByDisplayOrderAsc();
}