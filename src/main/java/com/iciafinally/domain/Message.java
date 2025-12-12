package com.iciafinally.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Message {
	@Id
	@GeneratedValue
	private Long id; // ID 필드
	private String sender; // 메시지 발신자
	private String receiver; // 메시지 수신자
	private String content; // 메시지 내용
	private LocalDateTime timestamp; // 메시지 전송 시각

	

}
