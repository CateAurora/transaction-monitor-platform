/**
 * 告警服务接口，提供告警发送功能。
 */
package com.trading.monitor.alert;

import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;

public interface AlertService {
    /**
     * 发送告警。
     * 处理逻辑：根据异常记录和分析结果发送告警。
     * @param record 异常记录对象，包含异常详细信息
     * @param analysis 分析结果对象，包含风险等级和修复建议
     */
    void sendAlert(ExceptionRecord record, AnalysisResult analysis);
}