/**
 * Bean配置类，定义RestTemplate和JavaMailSender Bean。
 */
package com.trading.monitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class BeanConfig {
    /**
     * 创建RestTemplate Bean。
     * 处理逻辑：返回新的RestTemplate实例。
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 创建JavaMailSender Bean。
     * 处理逻辑：创建JavaMailSenderImpl实例。
     * @return JavaMailSender实例
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 这些配置会从 application.yml 中读取
        // 这里只是创建一个默认的实例
        return mailSender;
    }
}