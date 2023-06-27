package com.sotska.interceptor;

import com.sotska.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestIdInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    private final RequestIdHolder requestIdHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        var authHeader = request.getHeader("Authorization");
        String requestId;
        var extractedToken = jwtUtils.extractToken(authHeader);
        if (extractedToken.isEmpty()) {
            MDC.put("email", "guest");
            MDC.put("requestId", UUID.randomUUID().toString());

            return true;
        }
        var token = extractedToken.get();
        var email = jwtUtils.extractUserName(token);
        var requestIdByEmail = requestIdHolder.getRequestId(email);

        if (requestIdByEmail.isPresent()) {
            requestId = requestIdByEmail.get();
        } else {
            requestId = UUID.randomUUID().toString();
            requestIdHolder.addRequestId(email, requestId);
        }
        MDC.put("email", email);
        MDC.put("requestId", requestId);

        return true;
    }
}
