package com.hxw.partnermatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 通信方式不同: HTTP协议是一种请求-响应协议，客户端发送请求给服务器，服务器返回响应。而WebSocket协议是一种全双工协议，客户端和服务器都可以主动发送消息。
 * 链接状态不同: HTTP协议是无状态的，每次请求都是独立的。而WebSocket协议是有状态的，建立了连接之后，客户端和服务器之间可以维持长连接。
 * 数据传输不同: HTTP协议是基于文本的，而WebSocket协议是基于二进制的。
 * 延迟不同: HTTP协议每次请求都需要建立连接，等待响应，传输数据，释放连接，这整个过程都需要一些时间。而WebSocket协议只需要建立一次连接，之后就可以高效地进行数据传输，所以延迟更小。
 */

@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
