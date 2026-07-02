package com.innowise.orderservice.kafka;


import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void listenPaymentEvents(PaymentCompletedEvent event) {
        orderService.updateOrderStatusFromPayment(event.getOrderId(), event.getStatus());
    }
}
