/**
 * 交易监控平台的主应用类，负责启动Spring Boot应用。
 */
package com.trading.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.trading.monitor.mapper")
@EnableScheduling
@EnableAsync
@EnableFeignClients
public class MonitorApplication {
    /**
     * 应用入口方法，启动Spring Boot应用。
     * 处理逻辑：调用SpringApplication.run启动应用。
     * @param args 命令行参数数组
     */
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
}