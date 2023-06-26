package com.sotska.security.controller;

import com.sotska.security.dto.LoginRequestDto;
import com.sotska.security.dto.LoginResponseDto;
import com.sotska.security.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @NonNull LoginRequestDto loginRequestDto) {
        log.info("Requested to login user: {}.", loginRequestDto.getEmail());
        var email = loginRequestDto.getEmail();
        try {
            var userDetails = securityService.login(email, loginRequestDto.getPassword());

            return LoginResponseDto.builder().nickName(email).token(userDetails).build();
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Credentials not correct.", ex);
        } catch (InternalAuthenticationServiceException ex) {
            throw new IllegalArgumentException("User with login: " + email + " is not exist", ex);
        }
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader String token, HttpServletRequest request, HttpServletResponse response) {
        log.info("Requested to logout user.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        securityService.logout(token);
    }
}
