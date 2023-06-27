package com.sotska.config;

import com.sotska.service.UserService;
import com.sotska.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        var extractedToken = jwtUtils.extractToken(authHeader);

        if (extractedToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = extractedToken.get();
        var email = jwtUtils.extractUserName(token);

        if ((email != null) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userService.loadUserByUsername(email);
            if (jwtUtils.isTokenValid(token, userDetails)) {
                var usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request, response);
            }
        }
    }
}

