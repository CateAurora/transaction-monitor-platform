/**
 * 异常事件监听器，监听异常事件并触发分析。
 */
package com.trading.monitor.listener;

import com.trading.monitor.analysis.AnalysisService;
import com.trading.monitor.event.ExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExceptionEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionEventListener.class);
    
    @Autowired
    private AnalysisService analysisService;
    
    /**
     * 处理异常事件。
     * 处理逻辑：异步调用分析服务对异常记录进行分析。
     * @param event 异常事件对象
     */
    @Async
    @EventListener
    public void handleExceptionEvent(ExceptionEvent event) {
        logger.info("Received exception event, starting analysis");
        analysisService.analyze(event.getExceptionRecord());
    }
}