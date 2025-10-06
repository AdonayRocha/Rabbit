package com.rabbit.Rabbit.model;

public record OrderItem(
    String productId,
    int quantity
) {}