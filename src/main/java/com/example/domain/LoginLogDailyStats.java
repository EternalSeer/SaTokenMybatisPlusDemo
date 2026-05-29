package com.example.domain;

import java.time.LocalDate;

public record LoginLogDailyStats(
        LocalDate statDate,
        String username,
        Long totalCount,
        Long successCount,
        Long failureCount,
        LocalDate firstLoginDate,
        LocalDate lastLoginDate
) {
}
