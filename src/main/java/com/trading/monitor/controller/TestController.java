/**
 * 测试控制器，提供测试接口。
 */
package com.trading.monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 发送测试异常消息。
     * 处理逻辑：创建测试异常消息对象，序列化为JSON，发送到Kafka主题。
     * @param request 测试请求对象，包含异常信息
     * @return 响应字符串，表示发送成功
     * @throws Exception 当序列化或发送失败时抛出异常
     */
    @PostMapping("/exception")
    public String sendTestException(@RequestBody TestExceptionRequest request) throws Exception {
        // 创建测试异常消息
        TestExceptionMessage message = new TestExceptionMessage();
        message.setServiceName(request.getServiceName());
        message.setExceptionType(request.getExceptionType());
        message.setErrorCode(request.getErrorCode());
        message.setMessage(request.getMessage());
        message.setStackTrace(request.getStackTrace());
        message.setTraceId(UUID.randomUUID().toString());
        message.setSpanId(UUID.randomUUID().toString());
        message.setTimestamp(LocalDateTime.now());
        
        // 序列化消息并发送到 Kafka
        String jsonMessage = objectMapper.writeValueAsString(message);
        kafkaTemplate.send("service-exceptions", jsonMessage);
        
        return "Test exception message sent successfully";
    }
    
    @Data
    public static class TestExceptionRequest {
        private String serviceName;
        private String exceptionType;
        private String errorCode;
        private String message;
        private String stackTrace;
    }
    
    @Data
    public static class TestExceptionMessage {
        private String serviceName;
        private String exceptionType;
        private String errorCode;
        private String message;
        private String stackTrace;
        private String traceId;
        private String spanId;
        private LocalDateTime timestamp;
    }
}