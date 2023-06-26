package com.sotska.interceptor;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestIdHolder {

    private Map<String, String> emailRequestIdMap = new ConcurrentHashMap<>();

    public void addRequestId(String email, String requestId) {
        emailRequestIdMap.put(email, requestId);
    }

    public Optional<String> getRequestId(String email) {
        return emailRequestIdMap.containsKey(email) ? Optional.of(emailRequestIdMap.get(email)) : Optional.empty();
    }

    public void removeRequestId(String email) {
        emailRequestIdMap.remove(email);
    }
}
