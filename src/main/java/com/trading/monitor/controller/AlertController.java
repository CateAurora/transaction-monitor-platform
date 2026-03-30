/**
 * 告警控制器，提供告警测试接口。
 */
package com.trading.monitor.controller;

import com.trading.monitor.alert.AlertService;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import lombok.Data;

@RestController
@RequestMapping("/api/alert")
public class AlertController {
    
    @Autowired
    private AlertService alertService;
    
    /**
     * 测试发送告警。
     * 处理逻辑：创建测试异常记录和分析结果，调用告警服务发送告警。
     * @param request 测试请求对象，包含服务名称和风险等级
     * @return 响应字符串，表示测试成功
     */
    @PostMapping("/test")
    public String testAlert(@RequestBody TestAlertRequest request) {
        // 创建测试异常记录
        ExceptionRecord record = new ExceptionRecord();
        record.setServiceName(request.getServiceName());
        record.setExceptionType("BUSINESS");
        record.setErrorCode("TEST-001");
        record.setMessage("Test exception message");
        record.setTraceId("test-trace-id");
        record.setSpanId("test-span-id");
        record.setTimestamp(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        
        // 创建测试分析结果
        AnalysisResult analysis = new AnalysisResult();
        analysis.setExceptionId(1L);
        analysis.setModelUsed("gpt-3.5-turbo");
        analysis.setRootCause("Test root cause analysis");
        analysis.setSuggestion("Test suggestion");
        analysis.setRiskLevel(request.getRiskLevel());
        analysis.setCreatedAt(LocalDateTime.now());
        
        // 发送预警
        alertService.sendAlert(record, analysis);
        
        return "Alert test sent successfully";
    }
    
    @Data
    public static class TestAlertRequest {
        private String serviceName;
        private String riskLevel;
    }
}