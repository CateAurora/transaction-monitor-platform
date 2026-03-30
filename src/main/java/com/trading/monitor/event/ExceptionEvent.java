/**
 * 异常事件类，用于发布异常发生事件。
 */
package com.trading.monitor.event;

import com.trading.monitor.entity.ExceptionRecord;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class ExceptionEvent extends ApplicationEvent {
    private final ExceptionRecord exceptionRecord;
    
    /**
     * 构造异常事件。
     * @param source 事件源对象
     * @param exceptionRecord 异常记录对象
     */
    public ExceptionEvent(Object source, ExceptionRecord exceptionRecord) {
        super(source);
        this.exceptionRecord = exceptionRecord;
    }
}