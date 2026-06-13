package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${user.service.url}",
        fallback = UserClientFallback.class
)
public interface UserClient {
    @GetMapping("/user-info")
    UserDto getUserByEmail(@RequestParam String email);

    @GetMapping("/user-info")
    UserDto getUserById(@RequestParam Long id);
}
