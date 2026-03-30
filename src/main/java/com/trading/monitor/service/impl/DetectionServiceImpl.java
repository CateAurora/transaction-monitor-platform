package com.trading.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trading.monitor.analysis.AnalysisService;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.mapper.ExceptionRecordMapper;
import com.trading.monitor.service.DetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DetectionServiceImpl implements DetectionService {

    private static final Logger logger = LoggerFactory.getLogger(DetectionServiceImpl.class);
    private static final int SPIKE_THRESHOLD = 10;

    @Autowired
    private ExceptionRecordMapper exceptionRecordMapper;

    @Autowired
    private AnalysisService analysisService;

    /**
     * 检测异常尖峰。
     * 处理逻辑：查询最近5分钟的异常记录，按服务分组统计，如果超过阈值则触发分析。
     */
    @Override
    @Scheduled(cron = "0 */5 * * * *") // 每5分钟执行一次
    public void detectSpike() {
        logger.info("Starting spike detection");

        // 查询最近5分钟的异常记录
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        LambdaQueryWrapper<ExceptionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(ExceptionRecord::getTimestamp, fiveMinutesAgo);
        List<ExceptionRecord> recentExceptions = exceptionRecordMapper.selectList(wrapper);

        // 按服务分组统计异常数量
        Map<String, Long> serviceExceptionCount = recentExceptions.stream()
                .collect(Collectors.groupingBy(ExceptionRecord::getServiceName, Collectors.counting()));

        // 检查是否超过阈值
        for (Map.Entry<String, Long> entry : serviceExceptionCount.entrySet()) {
            String serviceName = entry.getKey();
            long count = entry.getValue();

            if (count >= SPIKE_THRESHOLD) {
                logger.warn("Spike detected for service {}: {} exceptions in 5 minutes", serviceName, count);

                // 触发预警
                // TODO: 实现预警逻辑
                
                // 收集该服务的异常记录
                List<ExceptionRecord> serviceExceptions = recentExceptions.stream()
                        .filter(e -> e.getServiceName().equals(serviceName))
                        .toList();
                // 聚合异常信息
                StringBuilder exceptionsInfo = new StringBuilder();
                for (ExceptionRecord ex : serviceExceptions) {
                    exceptionsInfo.append("异常时间: ").append(ex.getTimestamp()).append(", 异常详情: ").append(ex.toString()).append("; ");
                }
                // 创建聚合的异常记录
                ExceptionRecord aggregatedException = new ExceptionRecord();
                aggregatedException.setServiceName(serviceName);
                aggregatedException.setTimestamp(serviceExceptions.get(serviceExceptions.size() - 1).getTimestamp()); // 使用最新时间
                aggregatedException.setMessage(exceptionsInfo.toString());
                AnalysisResult analysisResult = analysisService.analyze(aggregatedException);

                // 解析结果并记录
                if (analysisResult != null) {
                    logger.info("Analysis result for service {}: {}", serviceName, analysisResult.getSuggestion());
                }
                // 这里简化处理，使用最近的一条异常记录进行分析
                if (!recentExceptions.isEmpty()) {
                    ExceptionRecord latestException = recentExceptions.get(recentExceptions.size() - 1);
                    analysisService.analyze(latestException);
                }
            }
        }

        logger.info("Spike detection completed");
    }
}