/**
 * 邮件告警发送器，实现通过邮件发送告警消息。
 */
package com.trading.monitor.alert;

import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailAlertSender {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailAlertSender.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${alert.email.from}")
    private String from;
    
    /**
     * 发送邮件告警。
     * 处理逻辑：检查发件人配置，构建邮件内容，发送邮件。
     * @param record 异常记录对象，包含异常详细信息
     * @param analysis 分析结果对象，包含风险等级和修复建议
     */
    public void send(ExceptionRecord record, AnalysisResult analysis) {
        if (from == null || from.isEmpty()) {
            logger.warn("Email from address not configured, skipping");
            return;
        }
        
        // 构建邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo("admin@example.com"); // 收件人
        message.setSubject("服务异常预警 - " + record.getServiceName());
        message.setText(buildEmailContent(record, analysis));
        
        // 发送邮件
        mailSender.send(message);
        logger.info("Email alert sent");
    }
    
    /**
     * 构建邮件内容的文本。
     * 处理逻辑：根据异常记录和分析结果组装邮件正文字符串。
     * @param record 异常记录对象
     * @param analysis 分析结果对象
     * @return 邮件正文字符串
     */
    private String buildEmailContent(ExceptionRecord record, AnalysisResult analysis) {
        StringBuilder content = new StringBuilder();
        content.append("服务异常预警\n\n");
        content.append("服务名称: ").append(record.getServiceName()).append("\n");
        content.append("异常类型: ").append(record.getExceptionType()).append("\n");
        content.append("错误代码: ").append(record.getErrorCode()).append("\n");
        content.append("异常信息: ").append(record.getMessage()).append("\n");
        content.append("风险等级: ").append(analysis.getRiskLevel()).append("\n");
        content.append("根因分析: ").append(analysis.getRootCause()).append("\n");
        content.append("修复建议: ").append(analysis.getSuggestion()).append("\n");
        content.append("发生时间: ").append(record.getTimestamp()).append("\n");
        content.append("Trace ID: ").append(record.getTraceId()).append("\n");
        return content.toString();
    }
}