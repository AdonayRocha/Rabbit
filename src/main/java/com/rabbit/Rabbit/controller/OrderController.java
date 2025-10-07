package com.rabbit.Rabbit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbit.Rabbit.model.OrderCreatedMessage;
import com.rabbit.Rabbit.service.OrderPublisher;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderPublisher orderPublisher;

    public OrderController(OrderPublisher orderPublisher) {
        this.orderPublisher = orderPublisher;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderCreatedMessage message) {
        log.info("Received order creation request: " + message.orderId());
        orderPublisher.publishOrderCreated(message);
        return ResponseEntity.ok("Order message published: " + message.orderId());
    }
}