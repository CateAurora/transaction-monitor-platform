/**
 * 分析结果实体类，表示异常分析的结果。
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
@TableName("analysis_result")
public class AnalysisResult {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 异常ID */
    private Long exceptionId;
    /** 使用的模型 */
    private String modelUsed;
    /** 修复建议 */
    private String suggestion;
    /** 根因 */
    private String rootCause;
    /** 风险等级 */
    private String riskLevel;
    /** 创建时间 */
    private LocalDateTime createdAt;
}