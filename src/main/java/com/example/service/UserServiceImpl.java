package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.User;
import com.example.domain.UserCreateRequest;
import com.example.domain.UserUpdateRequest;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .last("limit 1")));
    }

    @Override
    public Page<User> searchPage(long current, long size, String keyword) {
        return page(new Page<>(current, size), keywordQuery(keyword)
                .orderByDesc(User::getCreatedAt));
    }

    @Override
    public List<User> listRecent(int limit) {
        return list(new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt)
                .last("limit " + Math.clamp(limit, 1, 100)));
    }

    @Override
    public List<User> listWithLoginLog(Boolean success, int limit) {
        String successCondition = success == null ? "" : " and l.success = " + success;
        return list(new LambdaQueryWrapper<User>()
                .exists("select 1 from sys_login_log l where l.user_id = sys_user.id and l.deleted = 0" + successCondition)
                .orderByAsc(User::getId)
                .last("limit " + Math.clamp(limit, 1, 100)));
    }

    @Override
    public long countByKeyword(String keyword) {
        return count(keywordQuery(keyword));
    }

    @Override
    public User createUser(UserCreateRequest request) {
        findByUsername(request.username()).ifPresent(user -> {
            throw new IllegalArgumentException("username already exists");
        });

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setNickname(request.nickname());
        user.setEmail(request.email());
        save(user);
        return user;
    }

    @Override
    public List<User> createUsers(List<UserCreateRequest> requests) {
        List<User> users = requests.stream()
                .map(request -> {
                    findByUsername(request.username()).ifPresent(user -> {
                        throw new IllegalArgumentException("username already exists: " + request.username());
                    });
                    User user = new User();
                    user.setUsername(request.username());
                    user.setPassword(request.password());
                    user.setNickname(request.nickname());
                    user.setEmail(request.email());
                    return user;
                })
                .toList();
        saveBatch(users);
        return users;
    }

    @Override
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = Optional.ofNullable(getById(id))
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (request.nickname() != null) {
            user.setNickname(request.nickname());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(request.password());
        }
        updateById(user);
        return user;
    }

    @Override
    public User updateEmail(Long id, String email) {
        boolean updated = lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getEmail, email)
                .set(User::getUpdatedAt, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new IllegalArgumentException("user not found");
        }
        return getById(id);
    }

    @Override
    public int deleteUsers(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return removeBatchByIds(ids) ? ids.size() : 0;
    }

    private LambdaQueryWrapper<User> keywordQuery(String keyword) {
        return new LambdaQueryWrapper<User>()
                .and(keyword != null && !keyword.isBlank(), wrapper -> wrapper
                        .like(User::getUsername, keyword)
                        .or()
                        .like(User::getNickname, keyword)
                        .or()
                        .like(User::getEmail, keyword));
    }
}
