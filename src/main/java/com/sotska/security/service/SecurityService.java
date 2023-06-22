package com.sotska.security.service;

import com.sotska.security.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String login(String email, String password) throws BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var userDetails = userService.loadUserByUsername(email);

        return jwtUtils.createToken(userDetails);
    }
}
