/**
 * 应用配置类，启用Feign客户端和异步处理。
 */
package com.trading.monitor.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableFeignClients
@EnableAsync
public class ApplicationConfig {
}