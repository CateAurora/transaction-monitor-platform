/**
 * 交易消费者，监听交易消息。
 */
package com.trading.monitor.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {
    /**
     * 消费交易消息。
     * 处理逻辑：打印交易消息。
     * @param message 交易消息字符串
     */
    @KafkaListener(topics = "transaction-topic", groupId = "transaction-monitor-group")
    public void consume(String message) {
        // 处理交易消息
        System.out.println("Received transaction message: " + message);
    }
}