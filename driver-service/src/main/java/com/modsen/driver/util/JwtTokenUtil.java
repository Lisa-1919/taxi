package com.modsen.driver.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtTokenUtil {

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No authentication information found");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaimAsString("sub");
    }

    public void validateAccess(UUID requestedId) {
        String currentUserId = getCurrentUserId();
        if (!requestedId.toString().equals(currentUserId)) {
            throw new AccessDeniedException(ExceptionMessages.ACCESS_DENIED.format());
        }
    }
}
