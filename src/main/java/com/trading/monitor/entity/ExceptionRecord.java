/**
 * 异常记录实体类，表示捕获到的异常信息。
 */
package com.trading.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("exception_record")
public class ExceptionRecord {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
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
    /** 创建时间 */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createdAt;
}