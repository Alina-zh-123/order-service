package com.innowise.orderservice.kafka;

import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {
    private final OrderService orderService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000)
    )
    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void listenPaymentEvents(PaymentCompletedEvent event) {
        orderService.updateOrderStatusFromPayment(event.getOrderId(), event.getStatus());
    }
}
