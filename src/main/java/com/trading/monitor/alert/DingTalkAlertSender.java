/**
 * 钉钉告警发送器，实现通过钉钉机器人发送告警消息。
 */
package com.trading.monitor.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DingTalkAlertSender {
    
    private static final Logger logger = LoggerFactory.getLogger(DingTalkAlertSender.class);
    
    @Value("${alert.dingtalk.webhook}")
    private String webhook;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 发送钉钉告警。
     * 处理逻辑：检查webhook配置，构建Markdown消息，发送HTTP请求到钉钉机器人。
     * @param record 异常记录对象，包含异常详细信息
     * @param analysis 分析结果对象，包含风险等级和修复建议
     * @throws Exception 当发送请求失败时抛出异常
     */
    public void send(ExceptionRecord record, AnalysisResult analysis) throws Exception {
        if (webhook == null || webhook.isEmpty()) {
            logger.warn("DingTalk webhook not configured, skipping");
            return;
        }
        
        // 构建 Markdown 消息
        String markdown = buildMarkdownMessage(record, analysis);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("msgtype", "markdown");
        
        Map<String, String> markdownMap = new HashMap<>();
        markdownMap.put("title", "服务异常预警");
        markdownMap.put("text", markdown);
        requestBody.put("markdown", markdownMap);
        
        // 发送请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(webhook, entity, String.class);
        
        logger.info("DingTalk alert sent, response: {}", response.getBody());
    }
    
    /**
     * 构建Markdown格式的告警消息内容。
     * 处理逻辑：根据异常记录和分析结果组装Markdown字符串，包含服务名称、异常类型、风险等级等信息。
     * @param record 异常记录对象
     * @param analysis 分析结果对象
     * @return Markdown格式的消息字符串
     */
    private String buildMarkdownMessage(ExceptionRecord record, AnalysisResult analysis) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("### 服务异常预警\n");
        markdown.append("**服务名称**: " + record.getServiceName() + "\n");
        markdown.append("**异常类型**: " + record.getExceptionType() + "\n");
        markdown.append("**错误代码**: " + record.getErrorCode() + "\n");
        markdown.append("**异常信息**: " + record.getMessage() + "\n");
        markdown.append("**风险等级**: " + analysis.getRiskLevel() + "\n");
        markdown.append("**根因分析**: " + analysis.getRootCause() + "\n");
        markdown.append("**修复建议**: " + analysis.getSuggestion() + "\n");
        markdown.append("**发生时间**: " + record.getTimestamp() + "\n");
        markdown.append("**Trace ID**: " + record.getTraceId() + "\n");
        return markdown.toString();
    }
}