package com.example.domain;

public record LoginResponse(Long userId, String username, String tokenName, String tokenValue) {
}
