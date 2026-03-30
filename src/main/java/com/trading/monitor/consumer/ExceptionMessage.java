/**
 * 异常消息类，表示从Kafka消费的异常消息。
 */
package com.trading.monitor.consumer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionMessage {
    /** 服务名称 */
    private String serviceName;
    /** 异常类型 */
    private String exceptionType;
    /** 错误代码 */
    private String errorCode;
    /** 异常信息 */
    private String message;
    /** 堆栈跟踪 */
    private String stackTrace;
    /** 跟踪ID */
    private String traceId;
    /** 跨度ID */
    private String spanId;
    /** 时间戳 */
    private LocalDateTime timestamp;
}