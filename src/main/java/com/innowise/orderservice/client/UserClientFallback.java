package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDto getUserByEmail(String email) {
        return new UserDto(
                null,
                null,
                email,
                "User service unavailable"
        );
    }

    @Override
    public UserDto getUserById(Long id) {
        return new UserDto(
                null,
                null,
                null,
                "User service unavailable"
        );
    }
}
