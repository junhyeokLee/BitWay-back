package com.example.bitway_back.util;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.security.UserDetailsImpl;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@NoArgsConstructor
public class SecurityUtil {
    // 현재 로그인된 객체 반환
    public static Optional<User> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return Optional.empty();
        }

        return Optional.of(((UserDetailsImpl) authentication.getPrincipal()).user());
    }

    public static String getCurrentUserId() {
        return getCurrentUser().map(user -> user.getId().toString()).orElse(null);
    }
}
