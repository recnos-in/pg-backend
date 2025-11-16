package org.recnos.pg.mapper;

import org.recnos.pg.model.dto.response.pg.*;
import org.recnos.pg.model.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PgMapper {

    public PgDetailResponse toDetailResponse(Pg pg, List<PgRoom> rooms, List<PgImage> images, List<PgAmenity> pgAmenities) {
        if (pg == null) {
            return null;
        }

        return PgDetailResponse.builder()
                .id(pg.getId())
                .ownerId(pg.getOwner() != null ? pg.getOwner().getId() : null)
                .ownerName(pg.getOwner() != null ? pg.getOwner().getName() : null)
                // Basic Information
                .name(pg.getName())
                .slug(pg.getSlug())
                .description(pg.getDescription())
                .summary(pg.getSummary())
                .propertyType(pg.getPropertyType())
                .totalFloors(pg.getTotalFloors())
                .totalRooms(pg.getTotalRooms())
                .establishmentYear(pg.getEstablishmentYear())
                // Location
                .address(pg.getAddress())
                .city(pg.getCity())
                .state(pg.getState())
                .pincode(pg.getPincode())
                .landmark(pg.getLandmark())
                .latitude(pg.getLatitude())
                .longitude(pg.getLongitude())
                .nearbyLocations(pg.getNearbyLocations())
                // Accommodation
                .genderType(pg.getGenderType())
                .occupancyType(pg.getOccupancyType())
                .furnishingType(pg.getFurnishingType())
                .securityDeposit(pg.getSecurityDeposit())
                .noticePeriodDays(pg.getNoticePeriodDays())
                // Food
                .foodAvailable(pg.getFoodAvailable())
                .foodType(pg.getFoodType())
                .foodPlans(pg.getFoodPlans())
                .foodDescription(pg.getFoodDescription())
                .foodPricing(pg.getFoodPricing())
                // Rules & Policies
                .houseRules(pg.getHouseRules())
                .cancellationPolicy(pg.getCancellationPolicy())
                .paymentTerms(pg.getPaymentTerms())
                .curfewTime(pg.getCurfewTime())
                .guestPolicy(pg.getGuestPolicy())
                // Media
                .floorPlanUrl(pg.getFloorPlanUrl())
                .virtualTourUrl(pg.getVirtualTourUrl())
                // Status
                .status(pg.getStatus())
                .approvalStatus(pg.getApprovalStatus())
                .approvalNotes(pg.getApprovalNotes())
                .approvedAt(pg.getApprovedAt())
                .approvedByName(pg.getApprovedBy() != null ? pg.getApprovedBy().getName() : null)
                .rejectedReason(pg.getRejectedReason())
                // Featured
                .isFeatured(pg.getIsFeatured())
                .featuredUntil(pg.getFeaturedUntil())
                .featuredLocations(pg.getFeaturedLocations())
                // Metrics
                .viewCount(pg.getViewCount())
                .favoriteCount(pg.getFavoriteCount())
                .contactClickCount(pg.getContactClickCount())
                .visitRequestCount(pg.getVisitRequestCount())
                .shareCount(pg.getShareCount())
                // SEO
                .metaTitle(pg.getMetaTitle())
                .metaDescription(pg.getMetaDescription())
                // Nested Data
                .rooms(rooms != null ? rooms.stream().map(this::toRoomDTO).collect(Collectors.toList()) : Collections.emptyList())
                .images(images != null ? images.stream().map(this::toImageDTO).collect(Collectors.toList()) : Collections.emptyList())
                .amenities(pgAmenities != null ? pgAmenities.stream().map(this::toAmenityDTO).collect(Collectors.toList()) : Collections.emptyList())
                // Timestamps
                .createdAt(pg.getCreatedAt())
                .updatedAt(pg.getUpdatedAt())
                .build();
    }

    public PgListResponse toListResponse(Pg pg, BigDecimal minPrice, BigDecimal maxPrice, Integer availableBeds, PgImage primaryImage) {
        if (pg == null) {
            return null;
        }

        return PgListResponse.builder()
                .id(pg.getId())
                .name(pg.getName())
                .slug(pg.getSlug())
                .summary(pg.getSummary())
                .propertyType(pg.getPropertyType())
                // Location
                .city(pg.getCity())
                .state(pg.getState())
                .landmark(pg.getLandmark())
                // Accommodation
                .genderType(pg.getGenderType())
                .occupancyType(pg.getOccupancyType())
                .furnishingType(pg.getFurnishingType())
                // Pricing
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                // Primary Image
                .primaryImageUrl(primaryImage != null ? primaryImage.getImageUrl() : null)
                .primaryThumbnailUrl(primaryImage != null ? primaryImage.getThumbnailUrl() : null)
                // Status
                .status(pg.getStatus())
                .approvalStatus(pg.getApprovalStatus())
                .isFeatured(pg.getIsFeatured())
                // Metrics
                .viewCount(pg.getViewCount())
                .favoriteCount(pg.getFavoriteCount())
                // Quick Info
                .foodAvailable(pg.getFoodAvailable())
                .totalRooms(pg.getTotalRooms())
                .availableBeds(availableBeds)
                // Timestamps
                .createdAt(pg.getCreatedAt())
                .build();
    }

    public PgRoomDTO toRoomDTO(PgRoom room) {
        if (room == null) {
            return null;
        }

        return PgRoomDTO.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .bedsPerRoom(room.getBedsPerRoom())
                .totalRooms(room.getTotalRooms())
                .availableBeds(room.getAvailableBeds())
                .pricePerBed(room.getPricePerBed())
                .pricePerMonth(room.getPricePerMonth())
                .roomSizeSqft(room.getRoomSizeSqft())
                .hasAttachedBathroom(room.getHasAttachedBathroom())
                .hasBalcony(room.getHasBalcony())
                .hasAc(room.getHasAc())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    public PgImageDTO toImageDTO(PgImage image) {
        if (image == null) {
            return null;
        }

        return PgImageDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .thumbnailUrl(image.getThumbnailUrl())
                .imageType(image.getImageType())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.getIsPrimary())
                .altText(image.getAltText())
                .uploadedAt(image.getUploadedAt())
                .build();
    }

    public AmenityDTO toAmenityDTO(PgAmenity pgAmenity) {
        if (pgAmenity == null || pgAmenity.getAmenity() == null) {
            return null;
        }

        Amenity amenity = pgAmenity.getAmenity();
        return AmenityDTO.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .category(amenity.getCategory())
                .iconName(amenity.getIconName())
                .isPaid(pgAmenity.getIsPaid())
                .price(pgAmenity.getPrice())
                .notes(pgAmenity.getNotes())
                .build();
    }
}