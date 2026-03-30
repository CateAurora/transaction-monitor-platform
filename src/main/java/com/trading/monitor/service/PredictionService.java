/**
 * 预测服务接口，提供异常预测功能。
 */
package com.trading.monitor.service;

public interface PredictionService {
    /**
     * 执行预测。
     * 处理逻辑：基于历史数据预测可能的异常。
     */
    void predict();
}