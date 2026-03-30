/**
 * 预测服务实现类，使用OpenAI进行异常预测。
 */
package com.trading.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.monitor.client.OpenAIClient;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.entity.PredictionResult;
import com.trading.monitor.mapper.ExceptionRecordMapper;
import com.trading.monitor.mapper.PredictionResultMapper;
import com.trading.monitor.service.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

@Service
public class PredictionServiceImpl implements PredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PredictionServiceImpl.class);
    
    @Autowired
    private ExceptionRecordMapper exceptionRecordMapper;
    
    @Autowired
    private PredictionResultMapper predictionResultMapper;
    
    @Autowired
    private OpenAIClient openAIClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    @Value("${openai.model}")
    private String openaiModel;
    
    /**
     * 执行预测。
     * 处理逻辑：查询过去24小时的异常记录，生成摘要，调用模型预测，解析并保存结果。
     */
    @Override
    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    public void predict() {
        logger.info("Starting exception prediction");
        
        // 查询过去24小时的异常记录
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        LambdaQueryWrapper<ExceptionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ExceptionRecord::getTimestamp, twentyFourHoursAgo);
        List<ExceptionRecord> recentExceptions = exceptionRecordMapper.selectList(wrapper);
        
        if (recentExceptions.isEmpty()) {
            logger.info("No exceptions found in the last 24 hours, skipping prediction");
            return;
        }
        
        // 生成异常数据摘要
        String summary = generateSummary(recentExceptions);
        
        // 调用大模型进行预测
        try {
            String prediction = getPredictionFromModel(summary);
            
            // 解析预测结果并保存
            parseAndSavePrediction(prediction);
        } catch (Exception e) {
            logger.error("Error during prediction: {}", e.getMessage(), e);
        }
        
        logger.info("Exception prediction completed");
    }
    
    /**
     * 生成异常数据摘要。
     * 处理逻辑：按服务分组，统计异常类型和数量。
     * @param exceptions 异常记录列表
     * @return 摘要字符串
     */
    private String generateSummary(List<ExceptionRecord> exceptions) {
        // 按服务分组
        Map<String, List<ExceptionRecord>> serviceExceptions = exceptions.stream()
                .collect(Collectors.groupingBy(ExceptionRecord::getServiceName));
        
        StringBuilder summary = new StringBuilder();
        summary.append("过去24小时的异常数据摘要：\n");
        
        for (Map.Entry<String, List<ExceptionRecord>> entry : serviceExceptions.entrySet()) {
            String serviceName = entry.getKey();
            List<ExceptionRecord> serviceExceptionList = entry.getValue();
            
            // 按异常类型分组
            Map<String, Long> exceptionTypeCount = serviceExceptionList.stream()
                    .collect(Collectors.groupingBy(ExceptionRecord::getExceptionType, Collectors.counting()));
            
            summary.append("服务：").append(serviceName).append("\n");
            summary.append("异常总数：").append(serviceExceptionList.size()).append("\n");
            summary.append("异常类型分布：\n");
            
            for (Map.Entry<String, Long> typeEntry : exceptionTypeCount.entrySet()) {
                summary.append("  - " + typeEntry.getKey() + ": " + typeEntry.getValue() + "次\n");
            }
            
            // 时间分布（简化处理）
            summary.append("时间分布：最近24小时内\n\n");
        }
        
        return summary.toString();
    }
    
    /**
     * 从模型获取预测。
     * 处理逻辑：构建prompt，调用OpenAI API。
     * @param summary 摘要字符串
     * @return 预测内容
     * @throws Exception 调用失败时抛出异常
     */
    private String getPredictionFromModel(String summary) throws Exception {
        String prompt = "基于以下过去24小时的异常数据摘要，预测未来1小时可能发生的异常类型及给出预防建议：\n" +
                summary +
                "\n请返回JSON格式，包含以下字段：\n" +
                "{\n" +
                "  \"serviceName\": \"服务名\",\n" +
                "  \"predictedExceptionType\": \"预测的异常类型\",\n" +
                "  \"predictionReason\": \"预测原因\",\n" +
                "  \"preventionSuggestion\": \"预防建议\"\n" +
                "}\n" +
                "如果有多个服务，请返回一个包含多个对象的数组。";
        
        OpenAIClient.OpenAIRequest request = new OpenAIClient.OpenAIRequest();
        request.setModel(openaiModel);
        request.setMessages(java.util.Arrays.asList(
                new OpenAIClient.Message("user", prompt)
        ));
        
        String authorization = "Bearer " + openaiApiKey;
        OpenAIClient.OpenAIResponse response = openAIClient.chatCompletions(authorization, request);
        
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        } else {
            throw new Exception("Invalid response from OpenAI API");
        }
    }
    
    /**
     * 解析并保存预测。
     * 处理逻辑：解析JSON，保存预测结果。
     * @param predictionContent 预测内容
     * @throws Exception 解析失败时抛出异常
     */
    private void parseAndSavePrediction(String predictionContent) throws Exception {
        // 提取 JSON 部分
        int startIndex = predictionContent.indexOf('[');
        int endIndex = predictionContent.lastIndexOf(']') + 1;
        
        if (startIndex != -1 && endIndex != -1) {
            // 数组格式
            String jsonArray = predictionContent.substring(startIndex, endIndex);
            PredictionResultDto[] dtos = objectMapper.readValue(jsonArray, PredictionResultDto[].class);
            
            for (PredictionResultDto dto : dtos) {
                savePredictionResult(dto);
            }
        } else {
            // 单个对象格式
            startIndex = predictionContent.indexOf('{');
            endIndex = predictionContent.lastIndexOf('}') + 1;
            if (startIndex != -1 && endIndex != -1) {
                String json = predictionContent.substring(startIndex, endIndex);
                PredictionResultDto dto = objectMapper.readValue(json, PredictionResultDto.class);
                savePredictionResult(dto);
            }
        }
    }
    
    /**
     * 保存预测结果。
     * 处理逻辑：创建PredictionResult对象并插入数据库。
     * @param dto 预测结果DTO
     */
    private void savePredictionResult(PredictionResultDto dto) {
        PredictionResult result = new PredictionResult();
        result.setServiceName(dto.getServiceName());
        result.setPredictedExceptionType(dto.getPredictedExceptionType());
        result.setPredictionReason(dto.getPredictionReason());
        result.setPreventionSuggestion(dto.getPreventionSuggestion());
        result.setPredictionTime(LocalDateTime.now());
        result.setPredictedPeriodStart(LocalDateTime.now());
        result.setPredictedPeriodEnd(LocalDateTime.now().plusHours(1));
        result.setCreatedAt(LocalDateTime.now());
        
        predictionResultMapper.insert(result);
        logger.info("Prediction result saved for service: {}", dto.getServiceName());
    }
    
    @Data
    private static class PredictionResultDto {
        private String serviceName;
        private String predictedExceptionType;
        private String predictionReason;
        private String preventionSuggestion;
    }
}