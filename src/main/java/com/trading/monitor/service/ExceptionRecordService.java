package com.trading.monitor.service;

/**
 * 异常记录服务接口，提供异常记录的管理功能。
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.trading.monitor.consumer.ExceptionMessage;
import com.trading.monitor.entity.ExceptionRecord;

public interface ExceptionRecordService extends IService<ExceptionRecord> {
    /**
     * 保存异常消息。
     * 处理逻辑：将异常消息转换为异常记录并保存到数据库。
     * @param message 异常消息对象
     * @return 保存是否成功
     */
    boolean save(ExceptionMessage message);
}