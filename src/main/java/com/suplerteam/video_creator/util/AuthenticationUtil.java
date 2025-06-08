package com.suplerteam.video_creator.util;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);

    @Autowired
    private UserRepository userRepository;

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            logger.error("Authentication context is empty");
            throw new IllegalStateException("Authentication context is empty");
        }
        // Skip authentication check for AnonymousAuthenticationToken
        if (authentication instanceof AnonymousAuthenticationToken) {
            logger.error("User is using an anonymous token");
            throw new IllegalStateException("No authenticated user found. Please log in first.");
        }

        String username;
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = authentication.getName();
        }
        
        if (username == null || username.isEmpty() || "anonymousUser".equals(username)) {
            throw new IllegalStateException("No authenticated user found. Please log in first.");
        }
        return username;
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found in database: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }
}
