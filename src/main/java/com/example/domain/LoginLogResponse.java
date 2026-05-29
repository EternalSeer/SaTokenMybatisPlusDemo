package com.example.domain;

import java.time.LocalDateTime;

public record LoginLogResponse(
        Long id,
        Long userId,
        String username,
        String ipAddress,
        String userAgent,
        Boolean success,
        String message,
        LocalDateTime createdAt
) {

    public static LoginLogResponse from(LoginLog log) {
        return new LoginLogResponse(
                log.getId(),
                log.getUserId(),
                log.getUsername(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getSuccess(),
                log.getMessage(),
                log.getCreatedAt()
        );
    }
}
