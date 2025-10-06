package com.rabbit.Rabbit.model;

import java.util.List;

public record OrderCreatedMessage(
    String orderId,
    String clientId,
    List<OrderItem> items
) {}