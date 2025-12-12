package com.iciafinally.socketUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MemberWebSocketHandler extends TextWebSocketHandler {
	
	private List<WebSocketSession> clientList = new ArrayList<>();
	

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("MemberWebSocketHandler - 접속!");
		clientList.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		clientList.remove(session);
	}

	public void loginAlert(String did) {
		for(WebSocketSession client : clientList) {
			Object loginId = client.getAttributes().get("loginId");
			if(loginId != null) {
				try {
					client.sendMessage( new TextMessage(did+"회원이 접속했습니다.") );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}








