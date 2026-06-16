package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${USER_SERVICE_URL:http://localhost:8081}",
        fallback = UserClientFallback.class
)
public interface UserClient {
    @GetMapping("/users")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
