/**
 * Kafka消费者服务，监听异常消息并处理。
 */
package com.trading.monitor.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.event.ExceptionEvent;
import com.trading.monitor.service.ExceptionRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ExceptionRecordService exceptionRecordService;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * 消费Kafka消息。
     * 处理逻辑：解析JSON消息，保存异常记录，发布异常事件。
     * @param message Kafka消息字符串
     */
    @KafkaListener(topics = "service-exceptions", groupId = "monitor-group")
    public void consume(String message) {
        logger.info("Received message: {}", message);
        
        try {
            // 解析 JSON 消消息
            ExceptionMessage exceptionMessage = objectMapper.readValue(message, ExceptionMessage.class);
            
            // 保存异常记录
            boolean saved = exceptionRecordService.save(exceptionMessage);
            
            if (saved) {
                logger.info("Exception record saved successfully");
                // 触发内部事件用于后续分析
                // 注意：这里简化处理，实际应该从数据库查询刚保存的记录
                ExceptionRecord record = new ExceptionRecord();
                record.setServiceName(exceptionMessage.getServiceName());
                record.setExceptionType(exceptionMessage.getExceptionType());
                record.setErrorCode(exceptionMessage.getErrorCode());
                record.setMessage(exceptionMessage.getMessage());
                record.setStackTrace(exceptionMessage.getStackTrace());
                record.setTraceId(exceptionMessage.getTraceId());
                record.setSpanId(exceptionMessage.getSpanId());
                record.setTimestamp(exceptionMessage.getTimestamp());
                
                eventPublisher.publishEvent(new ExceptionEvent(this, record));
            } else {
                logger.error("Failed to save exception record");
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message: {}", e.getMessage(), e);
        }
    }
}