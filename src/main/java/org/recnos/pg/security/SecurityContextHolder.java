package org.recnos.pg.security;

import org.recnos.pg.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.UUID;

public class SecurityContextHolder {

    public static UUID getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            try {
                return UUID.fromString((String) principal);
            } catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Invalid user ID in authentication");
            }
        }

        throw new UnauthorizedException("Invalid authentication principal");
    }

    private static Authentication getAuthentication() {
        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.getContext();
        return context != null ? context.getAuthentication() : null;
    }
}
