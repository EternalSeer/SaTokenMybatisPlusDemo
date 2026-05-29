package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.LoginLog;
import com.example.domain.LoginLogDailyStats;
import com.example.mapper.LoginLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public void record(Long userId, String username, String ipAddress, String userAgent, boolean success, String message) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setSuccess(success);
        log.setMessage(message);
        save(log);
    }

    @Override
    public Page<LoginLog> searchPage(
            long current,
            long size,
            Long userId,
            String username,
            Boolean success,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String keyword
    ) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .eq(userId != null, LoginLog::getUserId, userId)
                .eq(username != null && !username.isBlank(), LoginLog::getUsername, username)
                .eq(success != null, LoginLog::getSuccess, success)
                .ge(startTime != null, LoginLog::getCreatedAt, startTime)
                .le(endTime != null, LoginLog::getCreatedAt, endTime)
                .and(keyword != null && !keyword.isBlank(), nested -> nested
                        .like(LoginLog::getUsername, keyword)
                        .or()
                        .like(LoginLog::getIpAddress, keyword)
                        .or()
                        .like(LoginLog::getMessage, keyword))
                .orderByDesc(LoginLog::getCreatedAt);

        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<LoginLog> listLatestByUserIds(Collection<Long> userIds, int limit) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<LoginLog>()
                .select(
                        LoginLog::getId,
                        LoginLog::getUserId,
                        LoginLog::getUsername,
                        LoginLog::getSuccess,
                        LoginLog::getIpAddress,
                        LoginLog::getMessage,
                        LoginLog::getCreatedAt)
                .in(LoginLog::getUserId, userIds)
                .orderByDesc(LoginLog::getCreatedAt)
                .last("limit " + limit(limit)));
    }

    @Override
    public List<LoginLog> listFailedByUsernames(Collection<String> usernames, int limit) {
        if (usernames == null || usernames.isEmpty()) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<LoginLog>()
                .in(LoginLog::getUsername, usernames)
                .eq(LoginLog::getSuccess, false)
                .nested(wrapper -> wrapper
                        .like(LoginLog::getMessage, "invalid")
                        .or()
                        .like(LoginLog::getMessage, "password"))
                .orderByDesc(LoginLog::getCreatedAt)
                .last("limit " + limit(limit)));
    }

    @Override
    public List<LoginLog> listLogsOfExistingUsers(Boolean success, int limit) {
        return list(new LambdaQueryWrapper<LoginLog>()
                .eq(success != null, LoginLog::getSuccess, success)
                .inSql(LoginLog::getUserId, "select id from sys_user where deleted = 0")
                .orderByDesc(LoginLog::getCreatedAt)
                .last("limit " + limit(limit)));
    }

    @Override
    public Map<String, Long> countByUsername(Collection<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Map.of();
        }
        return usernames.stream()
                .collect(Collectors.toMap(
                        username -> username,
                        username -> count(new LambdaQueryWrapper<LoginLog>()
                                .eq(LoginLog::getUsername, username))
                ));
    }

    @Override
    public List<LoginLogDailyStats> dailyStats(
            String username,
            Boolean success,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return baseMapper.selectDailyStats(username, success, startTime, endTime);
    }

    @Override
    public int logicDeleteBefore(LocalDateTime beforeTime) {
        if (beforeTime == null) {
            return 0;
        }
        List<Long> ids = list(new LambdaQueryWrapper<LoginLog>()
                .select(LoginLog::getId)
                .lt(LoginLog::getCreatedAt, beforeTime))
                .stream()
                .map(LoginLog::getId)
                .toList();
        if (ids.isEmpty()) {
            return 0;
        }
        return removeBatchByIds(ids) ? ids.size() : 0;
    }

    private int limit(int limit) {
        return Math.max(1, Math.min(limit, 100));
    }
}
