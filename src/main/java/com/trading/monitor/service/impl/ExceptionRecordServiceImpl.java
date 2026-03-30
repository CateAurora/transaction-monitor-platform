/**
 * 异常记录服务实现类，实现异常记录的管理。
 */
package com.trading.monitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trading.monitor.consumer.ExceptionMessage;
import com.trading.monitor.entity.ExceptionRecord;
import com.trading.monitor.mapper.ExceptionRecordMapper;
import com.trading.monitor.service.ExceptionRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExceptionRecordServiceImpl extends ServiceImpl<ExceptionRecordMapper, ExceptionRecord> implements ExceptionRecordService {
    
    /**
     * 保存异常消息。
     * 处理逻辑：将异常消息转换为异常记录并插入数据库。
     * @param message 异常消息对象
     * @return 保存是否成功
     */
    @Override
    public boolean save(ExceptionMessage message) {
        ExceptionRecord record = new ExceptionRecord();
        record.setServiceName(message.getServiceName());
        record.setExceptionType(message.getExceptionType());
        record.setErrorCode(message.getErrorCode());
        record.setMessage(message.getMessage());
        record.setStackTrace(message.getStackTrace());
        record.setTraceId(message.getTraceId());
        record.setSpanId(message.getSpanId());
        record.setTimestamp(message.getTimestamp());
        record.setCreatedAt(LocalDateTime.now());
        return baseMapper.insert(record) > 0;
    }
}