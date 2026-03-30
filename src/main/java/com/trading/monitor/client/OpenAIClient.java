/**
 * OpenAI客户端接口，用于调用OpenAI API。
 */
package com.trading.monitor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;

@FeignClient(name = "openai", url = "${openai.url}")
public interface OpenAIClient {
    /**
     * 调用聊天完成API。
     * 处理逻辑：发送POST请求到OpenAI的chat/completions端点。
     * @param authorization 授权头字符串
     * @param request OpenAI请求对象
     * @return OpenAI响应对象
     */
    @PostMapping("/chat/completions")
    OpenAIResponse chatCompletions(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody OpenAIRequest request
    );
    
    @Data
    class OpenAIRequest {
        private String model;
        private List<Message> messages;
    }
    
    @Data
    @AllArgsConstructor
    class Message {
        private String role;
        private String content;
    }
    
    @Data
    class OpenAIResponse {
        private List<Choice> choices;
    }
    
    @Data
    class Choice {
        private Message message;
    }
}