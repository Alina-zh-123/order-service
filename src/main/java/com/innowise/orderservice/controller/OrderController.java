package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.OrderDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderDto orderDto,
                                                        @RequestParam String email) {
        return new ResponseEntity<>(orderService.createOrder(orderDto, email), HttpStatus.CREATED);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getOrdersWithFilter(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrdersWithFilter(start, end, status, pageable));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                                        @Valid @RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
    }
}
