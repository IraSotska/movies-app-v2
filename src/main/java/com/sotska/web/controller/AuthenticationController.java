package com.sotska.web.controller;

import com.sotska.service.SecurityService;
import com.sotska.web.dto.LoginRequestDto;
import com.sotska.web.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@Profile(value = {"dev", "prod"})
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;
    private final SecurityContextLogoutHandler securityContextLogoutHandler;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @NonNull LoginRequestDto loginRequestDto) {
        log.info("Requested to login user: {}.", loginRequestDto.getEmail());
        var email = loginRequestDto.getEmail();
        var userDetails = securityService.login(email, loginRequestDto.getPassword());

        return LoginResponseDto.builder().nickName(email).token(userDetails).build();
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader String token, HttpServletRequest request, HttpServletResponse response) {
        log.info("Requested to logout user.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            securityContextLogoutHandler.logout(request, response, auth);
        }
    }
}
