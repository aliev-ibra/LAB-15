package com.example.lab10.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvents {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEvents.class);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        logger.info("Login successful for user: {}", event.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        logger.warn("Login failed for user: {} - Reason: {}",
                event.getAuthentication().getName(), event.getException().getMessage());
    }

    // For Authorization failures (403) - Spring Security 6+
    @EventListener
    public void onAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        logger.warn("Unauthorized access attempt - Result: {}",
                event.getAuthorizationDecision());
    }
}
