package com.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.ApiResponse;
import com.example.domain.User;
import com.example.domain.UserCreateRequest;
import com.example.domain.UserEmailUpdateRequest;
import com.example.domain.UserResponse;
import com.example.domain.UserUpdateRequest;
import com.example.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> page(
            @RequestParam(defaultValue = "1") @Min(1) long current,
            @RequestParam(defaultValue = "10") @Min(1) long size,
            @RequestParam(required = false) String keyword
    ) {
        Page<User> page = userService.searchPage(current, size, keyword);
        List<UserResponse> records = page.getRecords().stream()
                .map(UserResponse::from)
                .toList();

        Page<UserResponse> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        responsePage.setRecords(records);
        return ApiResponse.ok(responsePage);
    }

    @GetMapping("/recent")
    public ApiResponse<List<UserResponse>> recent(
            @RequestParam(defaultValue = "5") @Min(1) int limit
    ) {
        return ApiResponse.ok(userService.listRecent(limit).stream()
                .map(UserResponse::from)
                .toList());
    }

    @GetMapping("/with-login-log")
    public ApiResponse<List<UserResponse>> withLoginLog(
            @RequestParam(required = false) Boolean success,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        return ApiResponse.ok(userService.listWithLoginLog(success, limit).stream()
                .map(UserResponse::from)
                .toList());
    }

    @GetMapping("/count")
    public ApiResponse<Long> count(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.countByKeyword(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return ApiResponse.ok(UserResponse.from(user));
    }

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.ok(UserResponse.from(userService.createUser(request)));
    }

    @PostMapping("/batch")
    public ApiResponse<List<UserResponse>> createBatch(
            @Valid @RequestBody List<UserCreateRequest> requests
    ) {
        return ApiResponse.ok(userService.createUsers(requests).stream()
                .map(UserResponse::from)
                .toList());
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ApiResponse.ok(UserResponse.from(userService.updateUser(id, request)));
    }

    @PutMapping("/{id}/email")
    public ApiResponse<UserResponse> updateEmail(
            @PathVariable Long id,
            @Valid @RequestBody UserEmailUpdateRequest request
    ) {
        return ApiResponse.ok(UserResponse.from(userService.updateEmail(id, request.email())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!userService.removeById(id)) {
            throw new IllegalArgumentException("user not found");
        }
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/batch")
    public ApiResponse<Integer> deleteBatch(@RequestBody List<Long> ids) {
        return ApiResponse.ok(userService.deleteUsers(ids));
    }
}
