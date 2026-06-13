package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    public static Specification<Order> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("createdAt"), start, end);
            } else if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), start);
            } else if (end != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), end);
            }
            return null;
        };
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isBlank()) return null;
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
