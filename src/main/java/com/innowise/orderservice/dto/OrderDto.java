package com.innowise.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Setter;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
public class OrderDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    @Positive(message = "Price must be positive")
    private BigDecimal totalPrice;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemDto> orderItems;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;
}
