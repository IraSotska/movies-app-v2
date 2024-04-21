package com.sotska.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoggerInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication == null ? "guest" : ((UserDetails) authentication.getPrincipal()).getUsername();

        MDC.put("email", username);
        MDC.put("requestId", UUID.randomUUID().toString());

        return true;
    }
}
