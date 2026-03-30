/**
 * 告警历史实体类，表示告警发送的历史记录。
 */
package com.trading.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("alert_history")
public class AlertHistory {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 异常ID */
    private Long exceptionId;
    /** 告警类型 */
    private String alertType;
    /** 接收者 */
    private String receiver;
    /** 发送状态 */
    private String status;
    /** 发送时间 */
    private LocalDateTime sentAt;
}