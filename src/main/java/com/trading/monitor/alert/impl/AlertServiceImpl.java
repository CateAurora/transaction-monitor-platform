/**
 * 告警服务实现类，提供告警发送的具体实现。
 */
package com.trading.monitor.alert.impl;

import com.trading.monitor.alert.AlertService;
import com.trading.monitor.alert.DingTalkAlertSender;
import com.trading.monitor.alert.EmailAlertSender;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.AlertHistory;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.mapper.AlertHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlertServiceImpl implements AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);
    
    @Autowired
    private DingTalkAlertSender dingTalkAlertSender;
    
    @Autowired
    private EmailAlertSender emailAlertSender;
    
    @Autowired
    private AlertHistoryMapper alertHistoryMapper;
    
    /**
     * 发送告警。
     * 处理逻辑：根据风险等级发送钉钉和邮件告警，并记录告警历史。
     * @param record 异常记录对象，包含异常详细信息
     * @param analysis 分析结果对象，包含风险等级和修复建议
     */
    @Override
    public void sendAlert(ExceptionRecord record, AnalysisResult analysis) {
        String riskLevel = analysis.getRiskLevel();
        
        // 根据风险等级决定通知方式
        // 这里简化处理，所有等级都立即发送
        try {
            // 发送钉钉通知
            dingTalkAlertSender.send(record, analysis);
            
            // 发送邮件通知
            emailAlertSender.send(record, analysis);
            
            // 记录预警历史
            recordAlertHistory(record, analysis, "SUCCESS");
        } catch (Exception e) {
            logger.error("Error sending alert: {}", e.getMessage(), e);
            recordAlertHistory(record, analysis, "FAILED");
        }
    }
    
    /**
     * 记录告警历史。
     * 处理逻辑：创建AlertHistory对象并插入数据库。
     * @param record 异常记录对象
     * @param analysis 分析结果对象
     * @param status 发送状态，如"SUCCESS"或"FAILED"
     */
    private void recordAlertHistory(ExceptionRecord record, AnalysisResult analysis, String status) {
        AlertHistory history = new AlertHistory();
        history.setExceptionId(record.getId());
        history.setAlertType("SYSTEM");
        history.setReceiver("admin");
        history.setStatus(status);
        history.setSentAt(LocalDateTime.now());
        
        alertHistoryMapper.insert(history);
    }
}