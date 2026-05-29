package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.common.ApiResponse;
import com.example.domain.LoginRequest;
import com.example.domain.LoginResponse;
import com.example.domain.User;
import com.example.domain.UserResponse;
import com.example.service.LoginLogService;
import com.example.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LoginLogService loginLogService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest
    ) {
        User user = userService.findByUsername(request.username())
                .filter(candidate -> candidate.getPassword().equals(request.password()))
                .orElse(null);

        if (user == null) {
            loginLogService.record(
                    null,
                    request.username(),
                    clientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    false,
                    "invalid username or password"
            );
            throw new IllegalArgumentException("invalid username or password");
        }

        StpUtil.login(user.getId());
        loginLogService.record(
                user.getId(),
                user.getUsername(),
                clientIp(servletRequest),
                servletRequest.getHeader("User-Agent"),
                true,
                "login success"
        );
        return ApiResponse.ok(new LoginResponse(
                user.getId(),
                user.getUsername(),
                StpUtil.getTokenName(),
                StpUtil.getTokenValue()
        ));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            StpUtil.logout();
            throw new IllegalArgumentException("user not found");
        }
        return ApiResponse.ok(UserResponse.from(user));
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
