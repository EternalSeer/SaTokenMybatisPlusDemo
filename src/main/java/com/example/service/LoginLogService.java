package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.domain.LoginLog;
import com.example.domain.LoginLogDailyStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LoginLogService extends IService<LoginLog> {

    void record(Long userId, String username, String ipAddress, String userAgent, boolean success, String message);

    Page<LoginLog> searchPage(
            long current,
            long size,
            Long userId,
            String username,
            Boolean success,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String keyword
    );

    List<LoginLog> listLatestByUserIds(Collection<Long> userIds, int limit);

    List<LoginLog> listFailedByUsernames(Collection<String> usernames, int limit);

    List<LoginLog> listLogsOfExistingUsers(Boolean success, int limit);

    Map<String, Long> countByUsername(Collection<String> usernames);

    List<LoginLogDailyStats> dailyStats(
            String username,
            Boolean success,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    int logicDeleteBefore(LocalDateTime beforeTime);
}
