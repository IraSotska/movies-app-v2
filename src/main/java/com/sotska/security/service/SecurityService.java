package com.sotska.security.service;

import com.sotska.repository.UserRepository;
import com.sotska.security.dto.LoginRequestDto;
import com.sotska.security.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private Map<String, String> sessions = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        var user = userRepository.findByEmail(loginRequestDto.getEmail());
        var encryptedPassword = passwordEncoder.encode(loginRequestDto.getPassword());
        var token = UUID.randomUUID().toString();
        sessions.put(user.getEmail(), token);

        if (!Objects.equals(user.getEncryptedPassword(), encryptedPassword)) {
            throw new IllegalArgumentException("Wrong password for user: " + user.getUserName());
        }

        return LoginResponseDto.builder().nickName(user.getNickName()).token(token).build();
    }

    public void logout(String token) {
        sessions.values().remove(token);
    }

    @Scheduled(fixedDelayString = "${cache.time-to-live.session}", timeUnit = TimeUnit.HOURS)
    private void logoutAllSessions() {
        sessions = new ConcurrentHashMap<>();
        log.info("All sessions was logout.");
    }
}
