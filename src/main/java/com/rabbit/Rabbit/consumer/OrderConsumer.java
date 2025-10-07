package com.rabbit.Rabbit.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbit.Rabbit.config.RabbitConfig;
import com.rabbit.Rabbit.model.OrderCreatedMessage;
import com.rabbitmq.client.Channel;

@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void processOrderCreated(OrderCreatedMessage message,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("Processing message: " + message);
            
            if (message.orderId() == null || message.orderId().trim().isEmpty()) {
                throw new IllegalArgumentException("OrderId cannot be null or empty");
            }
            
            if (message.clientId() == null || message.clientId().trim().isEmpty()) {
                throw new IllegalArgumentException("ClientId cannot be null or empty");
            }
            
            if (message.items() == null || message.items().isEmpty()) {
                throw new IllegalArgumentException("Order must have at least one item");
            }
            
            for (var item : message.items()) {
                if (item.quantity() <= 0) {
                    throw new IllegalArgumentException("Item quantity must be positive: " + item.productId());
                }
                if (item.productId() == null || item.productId().trim().isEmpty()) {
                    throw new IllegalArgumentException("ProductId cannot be null or empty");
                }
            }
            
            // Simulando processamento
            Thread.sleep(1000);
            log.info("Order processed successfully for orderId: " + message.orderId());
            
            channel.basicAck(tag, false);
            log.info("Message acknowledged successfully for orderId: " + message.orderId());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error processing order: " + message.orderId(), e);
            // decisão: requeue ou descartar
            boolean requeue = true; // requeue para erros de interrupção
            try {
                channel.basicNack(tag, false, requeue);
                log.warn("Message requeued due to interruption for orderId: " + message.orderId());
            } catch (Exception nackEx) {
                log.error("Failed to nack message for orderId: " + message.orderId(), nackEx);
            }
        } catch (Exception ex) {
            log.error("Unexpected error processing order: " + message.orderId(), ex);
            // decisão: requeue ou descartar
            boolean requeue = false; 
            try {
                channel.basicNack(tag, false, requeue);
                log.warn("Message rejected without requeue for orderId: " + message.orderId());
            } catch (Exception nackEx) {
                log.error("Failed to nack message for orderId: " + message.orderId(), nackEx);
            }
        }
    }
}