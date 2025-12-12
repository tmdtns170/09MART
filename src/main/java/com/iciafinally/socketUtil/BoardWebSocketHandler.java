package com.iciafinally.socketUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

public class BoardWebSocketHandler  extends TextWebSocketHandler {
	
	/* 접속한 클라이언트 목록 */
	List<WebSocketSession> clientList = new ArrayList<>();

	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		/* 클라이언트가 웹 소켓에 접속 했을 때 */
		System.out.println("BoardWebSocket - 클라이언트 접속!");
		clientList.add(session); // 접속 목록에 저장
		
		/* 
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "newReply");
		msgObj.put("msgInfo", "새 글 제목");
		Gson gson = new Gson(); // Gson 객체
		String msgObj_Json = gson.toJson(msgObj); // Map 객체를 Json String으로 변환
		session.sendMessage( new TextMessage( msgObj_Json ) );
		*/
		
		
	}
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		/* 접속된 클라이언트에서 메세지가 전송 되었을 때 */
		System.out.println("전송된 메세지 : " + message.getPayload());
		
		
		for( WebSocketSession client : clientList ) {
			if( !client.equals(session) ) { // 메세지를 전송한 클라이언트는 제외
				/* 서버에서 클라이언트에세 메세지 전송 */
				client.sendMessage(  new TextMessage( message.getPayload() ) );
			}
		}
	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		/* 클라이언트가 웹 소켓에서 접속을 해제 했을 때 */
		System.out.println("BoardWebSocket - 클라이언트 접속해제");
		clientList.remove(session); // 접속 목록에서 삭제
	}

	public void boardAlert(String title) {
		System.out.println("BoardWebSocketHandler - boardAlert() 호출");
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "newBoard");
		msgObj.put("msgInfo", title);
		
		for(WebSocketSession client : clientList) {
			try {
				client.sendMessage(  new TextMessage( new Gson().toJson(msgObj) ) );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void replyAlert(Long driver_id, Long board_id) {
		System.out.println("BoardWebSocketHandler - replyAlert()호출");
		/* 접속된 클라이언트에게 댓글 등록 알림 전송 */
		for( WebSocketSession client : clientList ) { // clientList에 게시글 작성자가 있는지 확인
			Object loginId = client.getAttributes().get("loginId");
			if(loginId != null) { // 로그인이 되어 있는 경우
				if( driver_id.equals( (Long)loginId)  ) { // clientList에 게시글 작성자가 있다면 알림 메세지 전송
					try {
						Map<String, String> msgObj = new HashMap<>();
						msgObj.put("msgType", "newReply");
						msgObj.put("msgInfo", board_id.toString());
						client.sendMessage( new TextMessage( new Gson().toJson(msgObj) ) );
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
	}
	
	
	
	
}
































