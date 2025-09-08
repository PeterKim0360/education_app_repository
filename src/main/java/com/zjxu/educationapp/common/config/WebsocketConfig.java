package com.zjxu.educationapp.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebsocketConfig {

    /**
     * 用于扫描和注册所有携带ServerEndPoint注解的实例
     * @return
     */
    /* NOTE 关于spring单元测试遇到的websocket的Bean创建错误
        当前注册Bean方法加入@ConditionalOnWebApplication 只在Web应用中生效
        在测试类加入@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
     */
    @ConditionalOnWebApplication  // 只在Web应用中生效
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
