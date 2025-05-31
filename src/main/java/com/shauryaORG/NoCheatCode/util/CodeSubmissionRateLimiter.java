package com.shauryaORG.NoCheatCode.util;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CodeSubmissionRateLimiter {
    private static final int LIMIT = 5;
    private static final long WINDOW_MILLIS = 60 * 60 * 1000; // 1 hour

    private static class UserRate {
        int count;
        long windowStart;
    }

    private final Map<String, UserRate> userRates = new ConcurrentHashMap<>();

    public synchronized boolean allow(String username) {
        long now = Instant.now().toEpochMilli();
        UserRate rate = userRates.computeIfAbsent(username, k -> new UserRate());
        if (now - rate.windowStart > WINDOW_MILLIS) {
            rate.count = 1;
            rate.windowStart = now;
            return true;
        } else {
            if (rate.count < LIMIT) {
                rate.count++;
                return true;
            } else {
                return false;
            }
        }
    }
}

