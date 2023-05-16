package com.sotska.security.controller;

import com.sotska.config.JwtUtils;
import com.sotska.security.dto.LoginRequestDto;
import com.sotska.security.service.SecurityService;
import com.sotska.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @NonNull LoginRequestDto loginRequestDto) {
        log.info("Requested to login user: {}.", loginRequestDto.getEmail());

        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

            var userDetails = userService.loadUserByUsername(loginRequestDto.getEmail());
            UserDetails user = (UserDetails) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION,
                            jwtUtils.createToken(userDetails))
                    .body(user.toString());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader String token) {
        log.info("Requested to logout user.");
        securityService.logout(token);
    }
}
