package com.innowise.orderservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderResponseDto {
    private OrderDto order;
    private UserDto user;
}