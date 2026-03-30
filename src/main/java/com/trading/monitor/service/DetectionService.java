/**
 * 检测服务接口，提供异常检测功能。
 */
package com.trading.monitor.service;

public interface DetectionService {
    /**
     * 检测异常尖峰。
     * 处理逻辑：检测交易或异常的尖峰。
     */
    void detectSpike();
}