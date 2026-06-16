package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public UserDto getUserByEmail(String email) {
        throw new IllegalStateException(
                "User service unavailable!"
        );
    }

    @Override
    public UserDto getUserById(Long id) {
        throw new IllegalStateException(
                "User service unavailable!"
        );
    }
}
