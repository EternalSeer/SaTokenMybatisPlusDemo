package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ApiResponse;
import com.example.domain.LoginLog;
import com.example.domain.LoginLogDailyStats;
import com.example.domain.LoginLogResponse;
import com.example.service.LoginLogService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/login-log")
@RequiredArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping
    public ApiResponse<Page<LoginLogResponse>> page(
            @RequestParam(defaultValue = "1") @Min(1) long current,
            @RequestParam(defaultValue = "10") @Min(1) long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String keyword
    ) {
        Page<LoginLog> page = loginLogService.searchPage(
                current,
                size,
                userId,
                username,
                success,
                startTime,
                endTime,
                keyword
        );
        Page<LoginLogResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        responsePage.setRecords(page.getRecords().stream()
                .map(LoginLogResponse::from)
                .toList());
        return ApiResponse.ok(responsePage);
    }

    @GetMapping("/latest")
    public ApiResponse<List<LoginLogResponse>> latestByUserIds(
            @RequestParam List<Long> userIds,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        return ApiResponse.ok(loginLogService.listLatestByUserIds(userIds, limit).stream()
                .map(LoginLogResponse::from)
                .toList());
    }

    @GetMapping("/failed")
    public ApiResponse<List<LoginLogResponse>> failedByUsernames(
            @RequestParam List<String> usernames,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        return ApiResponse.ok(loginLogService.listFailedByUsernames(usernames, limit).stream()
                .map(LoginLogResponse::from)
                .toList());
    }

    @GetMapping("/existing-user")
    public ApiResponse<List<LoginLogResponse>> logsOfExistingUsers(
            @RequestParam(required = false) Boolean success,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        return ApiResponse.ok(loginLogService.listLogsOfExistingUsers(success, limit).stream()
                .map(LoginLogResponse::from)
                .toList());
    }

    @GetMapping("/count-by-username")
    public ApiResponse<Map<String, Long>> countByUsername(@RequestParam List<String> usernames) {
        return ApiResponse.ok(loginLogService.countByUsername(usernames));
    }

    @GetMapping("/daily-stats")
    public ApiResponse<List<LoginLogDailyStats>> dailyStats(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        return ApiResponse.ok(loginLogService.dailyStats(username, success, startTime, endTime));
    }

    @DeleteMapping("/before")
    public ApiResponse<Integer> logicDeleteBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime
    ) {
        return ApiResponse.ok(loginLogService.logicDeleteBefore(beforeTime));
    }
}
