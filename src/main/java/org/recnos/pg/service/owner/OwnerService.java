package org.recnos.pg.service.owner;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.exception.ResourceNotFoundException;
import org.recnos.pg.mapper.OwnerMapper;
import org.recnos.pg.model.dto.response.owner.OwnerProfileResponse;
import org.recnos.pg.model.entity.Owner;
import org.recnos.pg.repository.OwnerRepository;
import org.recnos.pg.security.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    @Transactional(readOnly = true)
    public OwnerProfileResponse getCurrentOwnerProfile() {
        UUID ownerId = SecurityContextHolder.getCurrentUserId();
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        return ownerMapper.toProfileResponse(owner);
    }

    @Transactional(readOnly = true)
    public Owner findById(UUID ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
    }

    @Transactional(readOnly = true)
    public OwnerProfileResponse getOwnerProfileById(UUID ownerId) {
        Owner owner = findById(ownerId);
        return ownerMapper.toProfileResponse(owner);
    }
}