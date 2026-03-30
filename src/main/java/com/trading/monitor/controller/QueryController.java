/**
 * 查询控制器，提供数据查询接口。
 */
package com.trading.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.entity.PredictionResult;
import com.trading.monitor.mapper.AnalysisResultMapper;
import com.trading.monitor.mapper.ExceptionRecordMapper;
import com.trading.monitor.mapper.PredictionResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/query")
public class QueryController {
    
    @Autowired
    private ExceptionRecordMapper exceptionRecordMapper;
    
    @Autowired
    private AnalysisResultMapper analysisResultMapper;
    
    @Autowired
    private PredictionResultMapper predictionResultMapper;
    
    /**
     * 查询异常记录。
     * 处理逻辑：分页查询异常记录，支持按服务名称过滤，按时间倒序排序。
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param serviceName 服务名称，可选，用于过滤
     * @return 分页结果Map，包含总数、记录列表等
     */
    @GetMapping("/exceptions")
    public Map<String, Object> queryExceptions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String serviceName
    ) {
        Page<ExceptionRecord> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<ExceptionRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (serviceName != null && !serviceName.isEmpty()) {
            wrapper.eq(ExceptionRecord::getServiceName, serviceName);
        }
        
        wrapper.orderByDesc(ExceptionRecord::getTimestamp);
        Page<ExceptionRecord> result = exceptionRecordMapper.selectPage(pageObj, wrapper);
        
        return Map.of(
                "total", result.getTotal(),
                "records", result.getRecords(),
                "current", result.getCurrent(),
                "size", result.getSize(),
                "pages", result.getPages()
        );
    }
    
    /**
     * 查询分析结果。
     * 处理逻辑：分页查询分析结果，支持按异常ID过滤，按创建时间倒序排序。
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param exceptionId 异常ID，可选，用于过滤
     * @return 分页结果Map，包含总数、记录列表等
     */
    @GetMapping("/analysis")
    public Map<String, Object> queryAnalysisResults(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long exceptionId
    ) {
        Page<AnalysisResult> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<AnalysisResult> wrapper = new LambdaQueryWrapper<>();
        
        if (exceptionId != null) {
            wrapper.eq(AnalysisResult::getExceptionId, exceptionId);
        }
        
        wrapper.orderByDesc(AnalysisResult::getCreatedAt);
        Page<AnalysisResult> result = analysisResultMapper.selectPage(pageObj, wrapper);
        
        return Map.of(
                "total", result.getTotal(),
                "records", result.getRecords(),
                "current", result.getCurrent(),
                "size", result.getSize(),
                "pages", result.getPages()
        );
    }
    
    /**
     * 查询预测结果。
     * 处理逻辑：分页查询预测结果，支持按服务名称过滤，按预测时间倒序排序。
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param serviceName 服务名称，可选，用于过滤
     * @return 分页结果Map，包含总数、记录列表等
     */
    @GetMapping("/predictions")
    public Map<String, Object> queryPredictions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String serviceName
    ) {
        Page<PredictionResult> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PredictionResult> wrapper = new LambdaQueryWrapper<>();
        
        if (serviceName != null && !serviceName.isEmpty()) {
            wrapper.eq(PredictionResult::getServiceName, serviceName);
        }
        
        wrapper.orderByDesc(PredictionResult::getPredictionTime);
        Page<PredictionResult> result = predictionResultMapper.selectPage(pageObj, wrapper);
        
        return Map.of(
                "total", result.getTotal(),
                "records", result.getRecords(),
                "current", result.getCurrent(),
                "size", result.getSize(),
                "pages", result.getPages()
        );
    }
}