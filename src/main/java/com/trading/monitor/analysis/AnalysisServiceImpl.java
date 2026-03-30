/**
 * 分析服务实现类，使用OpenAI进行异常分析。
 */
package com.trading.monitor.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.monitor.alert.AlertService;
import com.trading.monitor.client.OpenAIClient;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.mapper.AnalysisResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import lombok.Data;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);
    
    @Autowired
    private OpenAIClient openAIClient;
    
    @Autowired
    private AnalysisResultMapper analysisResultMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private AlertService alertService;
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    @Value("${openai.model}")
    private String openaiModel;
    
    /**
     * 分析异常。
     * 处理逻辑：构建prompt，调用OpenAI API，解析响应，保存结果，发送告警。
     * @param record 异常记录对象
     * @return 分析结果对象，如果分析失败返回null
     */
    @Override
    public AnalysisResult analyze(ExceptionRecord record) {
        try {
            // 构建 prompt
            String prompt = buildPrompt(record);
            
            // 调用 OpenAI API
            OpenAIClient.OpenAIRequest request = new OpenAIClient.OpenAIRequest();
            request.setModel(openaiModel);
            request.setMessages(Collections.singletonList(
                    new OpenAIClient.Message("user", prompt)
            ));
            
            String authorization = "Bearer " + openaiApiKey;
            OpenAIClient.OpenAIResponse response = openAIClient.chatCompletions(authorization, request);
            
            // 解析响应
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                AnalysisResult analysisResult = parseAnalysisResult(content, record);
                
                // 保存分析结果
                analysisResultMapper.insert(analysisResult);
                logger.info("Analysis completed successfully for exception ID: {}", record.getId());
                
                // 发送预警通知
                alertService.sendAlert(record, analysisResult);
                
                return analysisResult;
            } else {
                logger.error("Invalid response from OpenAI API");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error analyzing exception: {}", e.getMessage(), e);
            // 可以在这里添加重试逻辑或标记分析失败
            return null;
        }
    }
    
    /**
     * 构建分析prompt。
     * 处理逻辑：根据异常记录组装prompt字符串。
     * @param record 异常记录对象
     * @return prompt字符串
     */
    private String buildPrompt(ExceptionRecord record) {
        return "Analyze the following exception and provide a JSON response with:\n" +
               "- rootCause: A detailed explanation of the root cause\n" +
               "- suggestion: Practical suggestions to fix the issue\n" +
               "- riskLevel: HIGH, MEDIUM, or LOW based on potential impact\n" +
               "\nException details:\n" +
               "Service: " + record.getServiceName() + "\n" +
               "Exception Type: " + record.getExceptionType() + "\n" +
               "Error Code: " + record.getErrorCode() + "\n" +
               "Message: " + record.getMessage() + "\n" +
               "Stack Trace: " + (record.getStackTrace() != null ? record.getStackTrace() : "N/A") + "\n" +
               "\nPlease return only the JSON without any additional text.";
    }
    
    /**
     * 解析分析结果。
     * 处理逻辑：从OpenAI响应中提取JSON并解析为AnalysisResult。
     * @param content OpenAI响应内容字符串
     * @param record 异常记录对象
     * @return AnalysisResult对象
     * @throws Exception 当JSON格式无效时抛出异常
     */
    private AnalysisResult parseAnalysisResult(String content, ExceptionRecord record) throws Exception {
        // 提取 JSON 部分
        int startIndex = content.indexOf('{');
        int endIndex = content.lastIndexOf('}') + 1;
        if (startIndex != -1 && endIndex != -1) {
            String json = content.substring(startIndex, endIndex);
            
            // 解析 JSON
            AnalysisResultDto dto = objectMapper.readValue(json, AnalysisResultDto.class);
            
            // 创建 AnalysisResult 实体
            AnalysisResult result = new AnalysisResult();
            result.setExceptionId(record.getId());
            result.setModelUsed(openaiModel);
            result.setRootCause(dto.getRootCause());
            result.setSuggestion(dto.getSuggestion());
            result.setRiskLevel(dto.getRiskLevel());
            result.setCreatedAt(LocalDateTime.now());
            
            return result;
        } else {
            throw new Exception("Invalid JSON format in response");
        }
    }
    
    @Data
    private static class AnalysisResultDto {
        private String rootCause;
        private String suggestion;
        private String riskLevel;
    }
}