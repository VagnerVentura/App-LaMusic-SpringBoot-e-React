package com.LaMusic.util;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;

import com.LaMusic.security.CustomUserDetails;

public class AuthUtils {

    public static UUID getLoggedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("Usuário não autenticado");
    }
}
