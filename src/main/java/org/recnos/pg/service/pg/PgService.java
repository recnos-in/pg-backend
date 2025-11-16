package org.recnos.pg.service.pg;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.exception.DuplicateResourceException;
import org.recnos.pg.exception.ResourceNotFoundException;
import org.recnos.pg.mapper.PgMapper;
import org.recnos.pg.model.dto.request.pg.PgCreateRequest;
import org.recnos.pg.model.dto.request.pg.PgImageRequest;
import org.recnos.pg.model.dto.request.pg.PgRoomRequest;
import org.recnos.pg.model.dto.request.pg.PgUpdateRequest;
import org.recnos.pg.model.dto.response.pg.PgDetailResponse;
import org.recnos.pg.model.dto.response.pg.PgListResponse;
import org.recnos.pg.model.entity.*;
import org.recnos.pg.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PgService {

    private final PgRepository pgRepository;
    private final PgRoomRepository pgRoomRepository;
    private final PgImageRepository pgImageRepository;
    private final PgAmenityRepository pgAmenityRepository;
    private final AmenityRepository amenityRepository;
    private final OwnerRepository ownerRepository;
    private final PgMapper pgMapper;

    // Create PG
    @Transactional
    public PgDetailResponse createPg(PgCreateRequest request) {
        // Validate owner exists
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));

        // Check slug uniqueness
        if (pgRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("PG with slug '" + request.getSlug() + "' already exists");
        }

        // Create PG entity
        Pg pg = new Pg();
        pg.setOwner(owner);
        pg.setName(request.getName());
        pg.setSlug(request.getSlug());
        pg.setDescription(request.getDescription());
        pg.setSummary(request.getSummary());
        pg.setPropertyType(request.getPropertyType());
        pg.setTotalFloors(request.getTotalFloors());
        pg.setTotalRooms(request.getTotalRooms());
        pg.setEstablishmentYear(request.getEstablishmentYear());

        // Location
        pg.setAddress(request.getAddress());
        pg.setCity(request.getCity());
        pg.setState(request.getState());
        pg.setPincode(request.getPincode());
        pg.setLandmark(request.getLandmark());
        pg.setLatitude(request.getLatitude());
        pg.setLongitude(request.getLongitude());
        pg.setNearbyLocations(request.getNearbyLocations());

        // Accommodation
        pg.setGenderType(request.getGenderType());
        pg.setOccupancyType(request.getOccupancyType());
        pg.setFurnishingType(request.getFurnishingType());
        pg.setSecurityDeposit(request.getSecurityDeposit());
        pg.setNoticePeriodDays(request.getNoticePeriodDays());

        // Food
        pg.setFoodAvailable(request.getFoodAvailable() != null ? request.getFoodAvailable() : false);
        pg.setFoodType(request.getFoodType());
        pg.setFoodPlans(request.getFoodPlans());
        pg.setFoodDescription(request.getFoodDescription());
        pg.setFoodPricing(request.getFoodPricing());

        // Rules & Policies
        pg.setHouseRules(request.getHouseRules());
        pg.setCancellationPolicy(request.getCancellationPolicy());
        pg.setPaymentTerms(request.getPaymentTerms());
        pg.setCurfewTime(request.getCurfewTime());
        pg.setGuestPolicy(request.getGuestPolicy());

        // Media
        pg.setFloorPlanUrl(request.getFloorPlanUrl());
        pg.setVirtualTourUrl(request.getVirtualTourUrl());

        // SEO
        pg.setMetaTitle(request.getMetaTitle());
        pg.setMetaDescription(request.getMetaDescription());

        // Status defaults
        pg.setStatus("draft");
        pg.setApprovalStatus("pending");
        pg.setIsFeatured(false);
        pg.setViewCount(0);
        pg.setFavoriteCount(0);
        pg.setContactClickCount(0);
        pg.setVisitRequestCount(0);
        pg.setShareCount(0);
        pg.setIsArchived(false);
        pg.setIsDeleted(false);

        // Timestamps
        pg.setCreatedAt(Instant.now());
        pg.setUpdatedAt(Instant.now());

        // Save PG
        Pg savedPg = pgRepository.save(pg);

        // Create rooms if provided
        List<PgRoom> rooms = new ArrayList<>();
        if (request.getRooms() != null && !request.getRooms().isEmpty()) {
            for (PgRoomRequest roomRequest : request.getRooms()) {
                PgRoom room = createPgRoom(savedPg, roomRequest);
                rooms.add(pgRoomRepository.save(room));
            }
        }

        // Create images if provided
        List<PgImage> images = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (PgImageRequest imageRequest : request.getImages()) {
                PgImage image = createPgImage(savedPg, imageRequest);
                images.add(pgImageRepository.save(image));
            }
        }

        // Create amenity associations if provided
        List<PgAmenity> pgAmenities = new ArrayList<>();
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            for (UUID amenityId : request.getAmenityIds()) {
                Amenity amenity = amenityRepository.findById(amenityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + amenityId));

                PgAmenity pgAmenity = new PgAmenity();
                PgAmenityId pgAmenityId = new PgAmenityId();
                pgAmenityId.setPgId(savedPg.getId());
                pgAmenityId.setAmenityId(amenityId);

                pgAmenity.setId(pgAmenityId);
                pgAmenity.setPg(savedPg);
                pgAmenity.setAmenity(amenity);
                pgAmenity.setIsPaid(false);
                pgAmenity.setCreatedAt(Instant.now());

                pgAmenities.add(pgAmenityRepository.save(pgAmenity));
            }
        }

        return pgMapper.toDetailResponse(savedPg, rooms, images, pgAmenities);
    }

    // Get PG by ID
    @Transactional(readOnly = true)
    public PgDetailResponse getPgById(UUID id) {
        Pg pg = findById(id);
        List<PgRoom> rooms = pgRoomRepository.findByPgId(id);
        List<PgImage> images = pgImageRepository.findByPgIdOrderByDisplayOrderAsc(id);
        List<PgAmenity> pgAmenities = pgAmenityRepository.findByPgId(id);

        return pgMapper.toDetailResponse(pg, rooms, images, pgAmenities);
    }

    // Get PG by slug
    @Transactional(readOnly = true)
    public PgDetailResponse getPgBySlug(String slug) {
        Pg pg = pgRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found with slug: " + slug));

        List<PgRoom> rooms = pgRoomRepository.findByPgId(pg.getId());
        List<PgImage> images = pgImageRepository.findByPgIdOrderByDisplayOrderAsc(pg.getId());
        List<PgAmenity> pgAmenities = pgAmenityRepository.findByPgId(pg.getId());

        // Increment view count
        pg.setViewCount(pg.getViewCount() + 1);
        pgRepository.save(pg);

        return pgMapper.toDetailResponse(pg, rooms, images, pgAmenities);
    }

    // Update PG
    @Transactional
    public PgDetailResponse updatePg(UUID id, PgUpdateRequest request) {
        Pg pg = findById(id);

        // Update fields if provided
        if (request.getName() != null) {
            pg.setName(request.getName());
        }
        if (request.getDescription() != null) {
            pg.setDescription(request.getDescription());
        }
        if (request.getSummary() != null) {
            pg.setSummary(request.getSummary());
        }
        if (request.getPropertyType() != null) {
            pg.setPropertyType(request.getPropertyType());
        }
        if (request.getTotalFloors() != null) {
            pg.setTotalFloors(request.getTotalFloors());
        }
        if (request.getTotalRooms() != null) {
            pg.setTotalRooms(request.getTotalRooms());
        }
        if (request.getEstablishmentYear() != null) {
            pg.setEstablishmentYear(request.getEstablishmentYear());
        }

        // Location
        if (request.getAddress() != null) {
            pg.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            pg.setCity(request.getCity());
        }
        if (request.getState() != null) {
            pg.setState(request.getState());
        }
        if (request.getPincode() != null) {
            pg.setPincode(request.getPincode());
        }
        if (request.getLandmark() != null) {
            pg.setLandmark(request.getLandmark());
        }
        if (request.getLatitude() != null) {
            pg.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            pg.setLongitude(request.getLongitude());
        }
        if (request.getNearbyLocations() != null) {
            pg.setNearbyLocations(request.getNearbyLocations());
        }

        // Accommodation
        if (request.getGenderType() != null) {
            pg.setGenderType(request.getGenderType());
        }
        if (request.getOccupancyType() != null) {
            pg.setOccupancyType(request.getOccupancyType());
        }
        if (request.getFurnishingType() != null) {
            pg.setFurnishingType(request.getFurnishingType());
        }
        if (request.getSecurityDeposit() != null) {
            pg.setSecurityDeposit(request.getSecurityDeposit());
        }
        if (request.getNoticePeriodDays() != null) {
            pg.setNoticePeriodDays(request.getNoticePeriodDays());
        }

        // Food
        if (request.getFoodAvailable() != null) {
            pg.setFoodAvailable(request.getFoodAvailable());
        }
        if (request.getFoodType() != null) {
            pg.setFoodType(request.getFoodType());
        }
        if (request.getFoodPlans() != null) {
            pg.setFoodPlans(request.getFoodPlans());
        }
        if (request.getFoodDescription() != null) {
            pg.setFoodDescription(request.getFoodDescription());
        }
        if (request.getFoodPricing() != null) {
            pg.setFoodPricing(request.getFoodPricing());
        }

        // Rules & Policies
        if (request.getHouseRules() != null) {
            pg.setHouseRules(request.getHouseRules());
        }
        if (request.getCancellationPolicy() != null) {
            pg.setCancellationPolicy(request.getCancellationPolicy());
        }
        if (request.getPaymentTerms() != null) {
            pg.setPaymentTerms(request.getPaymentTerms());
        }
        if (request.getCurfewTime() != null) {
            pg.setCurfewTime(request.getCurfewTime());
        }
        if (request.getGuestPolicy() != null) {
            pg.setGuestPolicy(request.getGuestPolicy());
        }

        // Media
        if (request.getFloorPlanUrl() != null) {
            pg.setFloorPlanUrl(request.getFloorPlanUrl());
        }
        if (request.getVirtualTourUrl() != null) {
            pg.setVirtualTourUrl(request.getVirtualTourUrl());
        }

        // SEO
        if (request.getMetaTitle() != null) {
            pg.setMetaTitle(request.getMetaTitle());
        }
        if (request.getMetaDescription() != null) {
            pg.setMetaDescription(request.getMetaDescription());
        }

        pg.setUpdatedAt(Instant.now());
        Pg updatedPg = pgRepository.save(pg);

        List<PgRoom> rooms = pgRoomRepository.findByPgId(id);
        List<PgImage> images = pgImageRepository.findByPgIdOrderByDisplayOrderAsc(id);
        List<PgAmenity> pgAmenities = pgAmenityRepository.findByPgId(id);

        return pgMapper.toDetailResponse(updatedPg, rooms, images, pgAmenities);
    }

    // Delete PG (soft delete)
    @Transactional
    public void deletePg(UUID id) {
        Pg pg = findById(id);
        pg.setIsDeleted(true);
        pg.setDeletedAt(Instant.now());
        pgRepository.save(pg);
    }

    // Get all PGs (paginated)
    @Transactional(readOnly = true)
    public Page<PgListResponse> getAllPgs(Pageable pageable) {
        Page<Pg> pgsPage = pgRepository.findAll(pageable);
        return convertToListResponsePage(pgsPage);
    }

    // Get PGs by owner
    @Transactional(readOnly = true)
    public Page<PgListResponse> getPgsByOwner(UUID ownerId, Pageable pageable) {
        Page<Pg> pgsPage = pgRepository.findByOwnerId(ownerId, pageable);
        return convertToListResponsePage(pgsPage);
    }

    // Get PGs by city
    @Transactional(readOnly = true)
    public Page<PgListResponse> getPgsByCity(String city, Pageable pageable) {
        Page<Pg> pgsPage = pgRepository.findByCity(city, pageable);
        return convertToListResponsePage(pgsPage);
    }

    // Search PGs
    @Transactional(readOnly = true)
    public Page<PgListResponse> searchPgs(String query, Pageable pageable) {
        Page<Pg> pgsPage = pgRepository.searchPgs(query, pageable);
        return convertToListResponsePage(pgsPage);
    }

    // Filter PGs
    @Transactional(readOnly = true)
    public Page<PgListResponse> filterPgs(
            String city,
            String state,
            String genderType,
            String occupancyType,
            String furnishingType,
            Boolean foodAvailable,
            String status,
            Pageable pageable) {
        Page<Pg> pgsPage = pgRepository.findByFilters(
                city, state, genderType, occupancyType, furnishingType, foodAvailable, status, pageable);
        return convertToListResponsePage(pgsPage);
    }

    // Helper methods
    private Pg findById(UUID id) {
        return pgRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PG not found with id: " + id));
    }

    private PgRoom createPgRoom(Pg pg, PgRoomRequest request) {
        PgRoom room = new PgRoom();
        room.setPg(pg);
        room.setRoomType(request.getRoomType());
        room.setBedsPerRoom(request.getBedsPerRoom());
        room.setTotalRooms(request.getTotalRooms());
        room.setAvailableBeds(request.getAvailableBeds());
        room.setPricePerBed(request.getPricePerBed());
        room.setPricePerMonth(request.getPricePerMonth());
        room.setRoomSizeSqft(request.getRoomSizeSqft());
        room.setHasAttachedBathroom(request.getHasAttachedBathroom() != null ? request.getHasAttachedBathroom() : false);
        room.setHasBalcony(request.getHasBalcony() != null ? request.getHasBalcony() : false);
        room.setHasAc(request.getHasAc() != null ? request.getHasAc() : false);
        room.setDescription(request.getDescription());
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());
        return room;
    }

    private PgImage createPgImage(Pg pg, PgImageRequest request) {
        PgImage image = new PgImage();
        image.setPg(pg);
        image.setImageUrl(request.getImageUrl());
        image.setThumbnailUrl(request.getThumbnailUrl());
        image.setImageType(request.getImageType());
        image.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        image.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false);
        image.setAltText(request.getAltText());
        image.setUploadedAt(Instant.now());
        return image;
    }

    private Page<PgListResponse> convertToListResponsePage(Page<Pg> pgsPage) {
        List<PgListResponse> responses = pgsPage.getContent().stream()
                .map(pg -> {
                    BigDecimal minPrice = pgRoomRepository.findMinPriceByPgId(pg.getId());
                    BigDecimal maxPrice = pgRoomRepository.findMaxPriceByPgId(pg.getId());
                    Integer availableBeds = pgRoomRepository.getTotalAvailableBedsByPgId(pg.getId());
                    PgImage primaryImage = pgImageRepository.findByPgIdAndIsPrimaryTrue(pg.getId()).orElse(null);

                    return pgMapper.toListResponse(pg, minPrice, maxPrice, availableBeds, primaryImage);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pgsPage.getPageable(), pgsPage.getTotalElements());
    }
}