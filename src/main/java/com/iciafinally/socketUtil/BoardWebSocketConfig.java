package com.iciafinally.socketUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class BoardWebSocketConfig implements WebSocketConfigurer  {
  
	
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(boardWebSocketHandler(), "/boardSocket")
		        .addInterceptors(new HttpSessionHandshakeInterceptor());
		
		registry.addHandler(memberWebSocketHandler(), "/memberSocket")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
		
		registry.addHandler(chatHandler(), "/memberChat")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
		 
	}
	 
	
	@Bean
	public ChatHandler chatHandler() {
		return new ChatHandler();
	}

	@Bean
	public BoardWebSocketHandler boardWebSocketHandler() {
		return new BoardWebSocketHandler();
	}
	
	@Bean
	public MemberWebSocketHandler memberWebSocketHandler() {
		return new MemberWebSocketHandler();
	}
	 

}













