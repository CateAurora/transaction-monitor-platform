package com.trading.monitor.analysis;

import com.trading.monitor.entity.AnalysisResult;
import com.trading.monitor.entity.ExceptionRecord;

/**
 * 分析服务接口，提供异常分析功能。
 */
public interface AnalysisService {
    /**
     * 分析异常。
     * 处理逻辑：对异常记录进行分析。
     * @param record 异常记录对象
     * @return 分析结果对象
     */
    AnalysisResult analyze(ExceptionRecord record);
}