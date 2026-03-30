/**
 * 预测结果实体类，表示异常预测的结果。
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
@TableName("prediction_result")
public class PredictionResult {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 服务名称 */
    private String serviceName;
    /** 预测的异常类型 */
    private String predictedExceptionType;
    /** 预测原因 */
    private String predictionReason;
    /** 预防建议 */
    private String preventionSuggestion;
    /** 预测时间 */
    private LocalDateTime predictionTime;
    /** 预测周期开始时间 */
    private LocalDateTime predictedPeriodStart;
    /** 预测周期结束时间 */
    private LocalDateTime predictedPeriodEnd;
    /** 创建时间 */
    private LocalDateTime createdAt;
}