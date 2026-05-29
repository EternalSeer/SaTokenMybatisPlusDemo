package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.domain.User;
import com.example.domain.UserCreateRequest;
import com.example.domain.UserUpdateRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService extends IService<User> {

    Optional<User> findByUsername(String username);

    Page<User> searchPage(long current, long size, String keyword);

    List<User> listRecent(int limit);

    List<User> listWithLoginLog(Boolean success, int limit);

    long countByKeyword(String keyword);

    User createUser(UserCreateRequest request);

    List<User> createUsers(List<UserCreateRequest> requests);

    User updateUser(Long id, UserUpdateRequest request);

    User updateEmail(Long id, String email);

    int deleteUsers(Collection<Long> ids);
}
