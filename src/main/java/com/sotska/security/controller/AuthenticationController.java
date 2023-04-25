package com.sotska.security.controller;

import com.sotska.security.dto.LoginRequestDto;
import com.sotska.security.dto.LoginResponseDto;
import com.sotska.security.service.SecurityService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @NonNull LoginRequestDto loginRequestDto) {
        log.info("Requested to login user: {}.", loginRequestDto.getEmail());
        return securityService.login(loginRequestDto);
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader String token) {
        log.info("Requested to logout user.");
        securityService.logout(token);
    }
}
