package com.example.nettySocketExample;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettySocketConfig {

    @Bean
    public SocketIOServer socketIOServer () {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(8081);
        return new SocketIOServer(config);
    }
}
