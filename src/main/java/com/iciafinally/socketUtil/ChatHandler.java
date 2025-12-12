package com.iciafinally.socketUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iciafinally.domain.Message;
import com.iciafinally.repository.MessageRepository;



public class ChatHandler extends TextWebSocketHandler {
    @Autowired
    private MessageRepository messageRepository; // 리포지토리 주입
    
    
	List<WebSocketSession> chatUserList = new ArrayList<>(); // 채팅에 접속한 유저 목록

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		/* 클라이언트 접속 */
		System.out.println("chatHandler - 클라이언트 접속!");
		Object loginDname = session.getAttributes().get("loginDname");// 로그인한 사용자 이름 가져오기
		Object loginAid = session.getAttributes().get("loginAid");// 로그인한 관리자 ID 가져오기
		System.out.println(loginDname);// 로그인한 사용자 이름 출력
		System.out.println(loginAid);// 로그인한 관리자 ID 출력
		// 사용자 이름 또는 관리자 이름 설정
		String userName = loginAid != null ? loginAid.toString() : loginDname.toString();
		boolean isAdmin = loginAid != null; // 관리자 여부 확인

		// 입장 메시지 전송 (관리자인지 여부에 따라)
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "inoutAlert");
		msgObj.put("msgInfo", userName + (isAdmin ? " 관리자님이 입장했습니다." : " 기사님이 입장했습니다."));
		 // 모든 접속 중인 사용자에게 입장 메시지 전송
		for (WebSocketSession chatUser : chatUserList) {
			chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
		}
		chatUserList.add(session); // 채팅 유저 목록에 추가

		/* 접속 중인 클라이언트들에게 채팅 유저 목록 전송 */
		sendChatUserList();
	    // 이전 메시지 출력
	    sendPreviousMessages(session);
	}

	private void sendPreviousMessages(WebSocketSession session) {
		 List<Message> messages = getAllMessages(); // 모든 메시지 가져오기
	        
	        // 각 메시지를 클라이언트에 전송
	        for (Message message : messages) {
	            Map<String, String> msgInfoObj = new HashMap<>();
	            msgInfoObj.put("msgDname", message.getSender()); // 발신자
	            msgInfoObj.put("msgContent", message.getContent()); // 메시지 내용
	            msgInfoObj.put("timestamp", message.getTimestamp().toString()); // 타임스탬프

	            
	            Map<String, String> msgObj = new HashMap<>();
	            
	            msgObj.put("msgType", "receiveChat"); // 메시지 유형 설정
	            
	            
	            msgObj.put("msgInfo", new Gson().toJson(msgInfoObj)); // 메시지 정보를 JSON으로 변환

	            
	            try {
	                session.sendMessage(new TextMessage(new Gson().toJson(msgObj))); // 각 메시지를 클라이언트에 전송
	            } catch (IOException e) {
	                e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
	            }
	        }
	    		
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		 // 클라이언트로부터 텍스트 메시지를 수신했을 때 호출되는 메서드
		Object loginDname = session.getAttributes().get("loginDname");
		Object loginAid = session.getAttributes().get("loginAid");
              
		//사용자 이름 결정
		String userName = loginAid != null ? loginAid.toString() : loginDname.toString();
		String msg = message.getPayload(); // 클라이언트에서 보낸 메시지 내용
		System.out.println("클라이언트에서 보낸 메세지: " + msg);// 수신한 메시지 출력
		
		
		JsonObject clientMsg = JsonParser.parseString(msg).getAsJsonObject();
		String chatType = clientMsg.get("chatType").getAsString();
		String chatInfo = clientMsg.get("chatInfo").getAsString();

		switch (chatType) {
		case "publicMsg":// 공개 메시지
			   saveMessage(userName, "all", chatInfo);// 데이터베이스에 메시지 저장
			sendChatMessage(userName, chatInfo, session); // 모든 클라이언트에 메시지 전송
			break;
		case "privateMsg":
			String sendTarget = clientMsg.get("sendTarget").getAsString(); // 메세지를 받을 대상
			  saveMessage(userName, sendTarget, chatInfo); // DB에 개인 메시지 저장
			sendChatMessagePrivate(userName, sendTarget, chatInfo);// 개인 메시지 전송
			break;
		case "publicImg":// 공개 이미지
			sendChatImg(userName, chatInfo, session);// 모든 클라이언트에 메시지 전송
			break;
		case "privateImg"://개인 이미지
			String sendTarget1 = clientMsg.get("sendTarget").getAsString(); // 메세지를 받을 대상
			sendChatImgPrivate(userName, sendTarget1, chatInfo);// 개인 메시지 전송
			break;
		}
	}
    // 메시지를 데이터베이스에 저장하는 메서드
	private void saveMessage(String sender, String receiver, String content) {
		Message message = new Message(); // 새로운 Message 객체 생성
		 message.setSender(sender); // 발신자 설정
	        message.setReceiver(receiver); // 수신자 설정
	        message.setContent(content); // 메시지 내용 설정
	        message.setTimestamp(LocalDateTime.now()); // 현재 시간으로 타임스탬프 설정
	        messageRepository.save(message); // DB에 메시지 저장
	}
	// 모든 메시지를 데이터베이스에서 가져오는 메서드
	private List<Message> getAllMessages() {
	    return messageRepository.findByOrderById(); // 모든 메시지 조회
	}
	 // 특정 사용자에게 개인 메시지를 전송하는 메서드
	private void sendChatMessagePrivate(String userName, String sendTarget, String chatInfo) {
		System.out.println("보내는 사용자: " + userName);
		System.out.println("메세지를 받을 사용자: " + sendTarget);
		System.out.println("메세지 내용: " + chatInfo);
       
		Map<String, String> msgInfoObj = new HashMap<>();
		msgInfoObj.put("msgDname", userName);
		msgInfoObj.put("msgContent", chatInfo);
		msgInfoObj.put("msgType", "privateChat"); // 메시지 유형 추가
		
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "privateChat");
		msgObj.put("msgInfo", new Gson().toJson(msgInfoObj));

		for (WebSocketSession chatUser : chatUserList) {
		    String aid = (String) chatUser.getAttributes().get("loginAid");
		    String dname = (String) chatUser.getAttributes().get("loginDname");

		    if (sendTarget.equals(aid) || sendTarget.equals(dname)) {
		        try {
		            chatUser.sendMessage(new TextMessage(new Gson().toJson(msgInfoObj)));
		            break;  // 메세지를 보낸 후 루프 종료
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}

	}

	private void sendChatMessage(String userName, String msg, WebSocketSession session) {
		Map<String, String> msgInfoObj = new HashMap<>();
		msgInfoObj.put("msgDname", userName); // 보낸 사람 이름
		msgInfoObj.put("msgContent", msg); // 메세지 내용
		msgInfoObj.put("timestamp", LocalDateTime.now().toString()); // 타임스탬프

		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "receiveChat"); // 메세지 유형: 공개 메세지
		msgObj.put("msgInfo", new Gson().toJson(msgInfoObj));

		for (WebSocketSession chatUser : chatUserList) {
			if (chatUser.equals(session)) {
				continue; // 자신에게는 메세지를 보내지 않음
			}
			try {
				chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendChatImg(String userName, String msg, WebSocketSession session) {
		Map<String, String> msgInfoObj = new HashMap<>();
		msgInfoObj.put("msgDname", userName);
		msgInfoObj.put("msgContent", msg);

		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "receiveImg");
		msgObj.put("msgInfo", new Gson().toJson(msgInfoObj));

		for (WebSocketSession chatUser : chatUserList) {
			if (chatUser.equals(session)) {
				continue;
			}
			try {
				chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendChatImgPrivate(String userName, String sendTarget, String chatInfo) {
		System.out.println("보내는 사용자: " + userName);
		System.out.println("메세지를 받을 사용자: " + sendTarget);
		System.out.println("메세지 내용: " + chatInfo);

		Map<String, String> msgInfoObj = new HashMap<>();
		msgInfoObj.put("msgDname", userName);
		msgInfoObj.put("msgContent", chatInfo);

		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "privateImg");
		msgObj.put("msgInfo", new Gson().toJson(msgInfoObj));

		for (WebSocketSession chatUser : chatUserList) {
			if (sendTarget.equals(chatUser.getAttributes().get("loginDname").toString())) {
				try {
					chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		chatUserList.remove(session);
		sendChatUserList();

		/* 클라이언트 접속 */
		System.out.println("chatHandler - 클라이언트 접속!");
		Object loginDname = session.getAttributes().get("loginDname");
		Object loginAid = session.getAttributes().get("loginAid");

		// 사용자 이름 또는 관리자 이름 설정
		String userName = loginAid != null ? loginAid.toString() : loginDname.toString();
		boolean isAdmin = loginAid != null;

		// 입장 메시지 전송 (관리자인지 여부에 따라)
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "inoutAlert");
		msgObj.put("msgInfo", userName + (isAdmin ? " 관리자님이 퇴장했습니다." : " 기사님이 퇴장했습니다."));
		for (WebSocketSession chatUser : chatUserList) {
			chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
		}

	}

	private void sendChatUserList() {
		List<String> userList = new ArrayList<>();
		for (WebSocketSession chatUser : chatUserList) {
			Object loginDname = chatUser.getAttributes().get("loginDname");
			Object loginAid = chatUser.getAttributes().get("loginAid");
			String userName = loginAid != null ? loginAid.toString() : loginDname.toString();
			userList.add(userName);
		}
		Map<String, String> msgObj = new HashMap<>();
		msgObj.put("msgType", "userList");
		msgObj.put("msgInfo", new Gson().toJson(userList));
		for (WebSocketSession chatUser : chatUserList) {
			try {
				chatUser.sendMessage(new TextMessage(new Gson().toJson(msgObj)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}