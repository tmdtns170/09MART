const chatViewDiv = document.getElementById('chatView'); // 채팅 메세지가 나타나는 DIV
const sendTargetDiv = document.getElementById('sendTargetBox'); // privateMsg를 받을 대상이 나타나는 DIV 
function sendChatImg(inputImgEl) {
	let file = inputImgEl.files[0];
	if (file) {
		// ajax - 파일 업로드 >> 파일이 저장된 경로 / 파일명 응답
		let formData = new FormData();
		formData.append('imgFile', file);
		$.ajax({
			url: '/chatImgUpload',
			type: 'post',
			data: formData,
			contentType: false,
			processData: false,
			async: false,
			success: function (res) {
				imgLoadCheck(res); // 이미지 확인 기능 호출
			}
		});
	}
}

// 이미지 확인 기능
function imgLoadCheck(imgPath) {
	let img = new Image();
	let imgCheck = setInterval(function () {
		img.src = imgPath; // img src 지정
		img.onload = function () {
			console.log('이미지 로딩 완료');
			sendImgMessage(imgPath);
			clearInterval(imgCheck);
		}
		img.onerror = function () {
			console.log('이미지 로딩 실패');
		}
	}, 300);
}

// 내가 전송한 이미지를 화면에 출력
function printSendChatImg(imgPath) {
	let sendImgDiv = document.createElement('div');
	sendImgDiv.classList.add('sendMsg', 'my-2');
	sendImgDiv.innerHTML = `<img src="${imgPath}" onerror="imgLoadCheck(this)" class="msgBox p-2"></img>`;
	chatViewDiv.appendChild(sendImgDiv);
	chatViewDiv.scrollTop = chatViewDiv.scrollHeight;
}

function sendImgMessage(imgPath) {
	// chatObj 생성
	let chatObj = {};
	// chatType 설정
	let sendTargetMember = document.getElementById('sendTargetMember');
	if (sendTargetMember == null) {
		chatObj.chatType = 'publicImg';
	} else {
		chatObj.chatType = 'privateImg';
		chatObj.sendTarget = sendTargetMember.innerText;
	}
	chatObj.chatInfo = imgPath;
	let chatObj_String = JSON.stringify(chatObj);
	chatSocket.send(chatObj_String);

	if (sendTargetMember == null) {
		printSendChatImg(imgPath);
	} else {
		let targetMember = sendTargetMember.value;
		let sendMsgDiv = document.createElement('div');
		sendMsgDiv.classList.add('privateSendMsg', 'my-2', 'p-2');
		sendMsgDiv.innerHTML = `<div>${targetMember}에게 개인 메세지</div>
                          <img src="${imgPath}" onerror="imgLoadCheck(this)" class="msgBox p-2"></img>`;
		chatViewDiv.appendChild(sendMsgDiv);
	}
}
// 메시지 화면에 표시하는 함수
function displayMessage(sender, content, timestamp) {
	const messageElement = document.createElement('div'); // 새 div 요소 생성
	messageElement.innerText = `${timestamp} - ${sender}: ${content}`; // 메시지 내용 설정
	chatViewDiv.appendChild(messageElement); // 채팅 창에 메시지 추가
	chatViewDiv.scrollTop = chatViewDiv.scrollHeight;  // 스크롤을 아래로
}

// 메시지를 화면에 출력하는 함수
function printSendChatMsg(inputMsg, isPrivate = false, targetMember = '') {
	console.log("printSendChatMsg")
	console.log(inputMsg)
	let sendMsgDiv = document.createElement('div');
	sendMsgDiv.classList.add(isPrivate ? 'privateSendMsg' : 'sendMsg', 'my-2', 'p-2');
	const date = new Date(inputMsg.timestamp);
	const timestamp = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }); // 현재 시간 가져오기
	console.log(timestamp)
	
	if (isPrivate) {
		sendMsgDiv.innerHTML = `
	               <div>${targetMember}에게 개인 메세지</div>
	               <div class="msgBox p-2">${inputMsg.msgContent}</div>
				   <div class="timestamp">${timestamp}</div>`;
				   
	} else {
		sendMsgDiv.innerHTML = `<div class="msgBox p-2">${inputMsg.msgContent}</div>
			   <div class="timestamp">${timestamp}</div>`;
	}

	chatViewDiv.appendChild(sendMsgDiv);
	chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
}
// 텍스트 메세지 전송기능
function sendChatMsg(event) {
	let inputEl = event.target; // 메세지 입력 태그
	if (event.keyCode == 13) { // 'Enter' keyCode : 13
		let inputMsg = inputEl.value; // 메세지 입력 태그에 입력한 값
		console.log("입력한 채팅 메세지 : " + inputMsg);
		// chatObj 생성
		let chatObj = {};
		// chatType 설정
		let sendTargetMember = document.getElementById('sendTargetMember');

		if (sendTargetMember == null) {
			chatObj.chatType = 'publicMsg';
		} else {
			chatObj.chatType = 'privateMsg';
			chatObj.sendTarget = sendTargetMember.innerText;
		}
		// chatInfo 설정
		chatObj.chatInfo = inputMsg;
		chatObj.msgContent = inputMsg;
		chatObj.timestamp = new Date();
		// chatObj >> json_String 변환
		let chatObj_String = JSON.stringify(chatObj);
		// 채팅소켓에 입력한 채팅 메세지를 전송
		chatSocket.send(chatObj_String);

		if (sendTargetMember == null) {
			printSendChatMsg(chatObj);
		} else {

			printSendChatMsg(inputMsg, true, sendTargetMember.innerText);
		}
		inputEl.value = "";
	}
}

// WebSocket 설정


let chatSocket = new WebSocket("/memberChat");
chatSocket.onopen = function (event) {
	console.log('memberChat 접속');
}
chatSocket.onclose = function (event) {
	alert('접속이 해제 되었습니다');
	//location.href = "/";
}

chatSocket.onmessage = function (event) {
	console.log('받은 메세지');
	let serverMsg = JSON.parse(event.data);
	console.log("serverMsg");
	console.log(serverMsg);
	// 채팅 웹소켓 서버로부터 받은 메세지 처리
	switch (serverMsg.msgType) {
		case 'userList':
			let userListObj = JSON.parse(serverMsg.msgInfo);
			refreshUserList(userListObj);
			chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			break;

		case 'receiveChat':
			let receiveMsg = JSON.parse(serverMsg.msgInfo);
			console.log("receiveMsg");
			console.log(receiveMsg);
			console.log(myId);
			if (receiveMsg.msgDname == myId) {
				console.log('내가 보낸 메세지')
				printSendChatMsg(receiveMsg);
				chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			} else {
				console.log('받은 메세지')
				printReceiveChatMsg(receiveMsg);
				chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			}
			break;

		case 'inoutAlert':
			printInoutUser(serverMsg.msgInfo);
			chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			break;

		case 'privateChat':
			// 받는 사람도 개인 메세지를 화면에 출력하도록 수정
			let privateMsg = JSON.parse(serverMsg.msgInfo);
			printPrivateChatMsg(privateMsg);
			chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			break;

		case 'receiveImg':
			let receiveImg = JSON.parse(serverMsg.msgInfo);
			printReceiveImg(receiveImg);
			chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			break;

		case 'privateImg':
			let privateImg = JSON.parse(serverMsg.msgInfo);
			printPrivateImg(privateImg);
			chatViewDiv.scrollTop = chatViewDiv.scrollHeight; // 스크롤을 아래로
			break;
	}
}

/* 입/퇴장 메세지 출력 */
function printInoutUser(inoutMsg) {
	let inoutDiv = document.createElement('div');
	inoutDiv.classList.add('text-center', 'my-2');
	inoutDiv.innerHTML = `<div class="inout d-inline-block p-1 px-3 rounded">${inoutMsg}</div>`;
	chatViewDiv.appendChild(inoutDiv);
}

/* 받은 채팅 메세지 출력 */
function printReceiveChatMsg(receiveMsg) {
	console.log("receiveMsg")
	console.log(receiveMsg)
	let receiveMsgDiv = document.createElement('div');

	// 메시지가 수신된 사용자와 현재 사용자 비교
	let isSender = receiveMsg.msgDname === myId; // 현재 사용자 이름에 맞게 변경

	receiveMsgDiv.classList.add('receiveMsg', 'my-2');
	
	const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }); // 현재 시간 가져오기


	receiveMsgDiv.innerHTML = `
<div class="msgId">${receiveMsg.msgDname}</div>
<div class="msgBox p-2">${receiveMsg.msgContent}</div>
<div class="timestamp">${receiveMsg.timestamp}</div>`;
	chatViewDiv.appendChild(receiveMsgDiv);
}
/* 받은 채팅 메세지 출력 */
function printReceiveChatMsg(receiveMsg) {
    console.log("receiveMsg");
    console.log(receiveMsg);
    let receiveMsgDiv = document.createElement('div');

    // 메시지가 수신된 사용자와 현재 사용자 비교
    let isSender = receiveMsg.msgDname === myId; // 현재 사용자 이름에 맞게 변경

    receiveMsgDiv.classList.add('receiveMsg', 'my-2');

    // timestamp를 Date 객체로 변환하고, 시간을 포맷팅
    const date = new Date(receiveMsg.timestamp);
	
   //오전 /오후 형식으로 시간을 포맷팅
   const options = {hour: 'numeric',minute: 'numeric',hour12: true};
   const formattedTimestamp = date.toLocaleTimeString([],options);
   
    receiveMsgDiv.innerHTML = `
        <div class="msgId">${receiveMsg.msgDname}</div>
        <div class="msgBox p-2">${receiveMsg.msgContent}</div>
        <div class="timestamp">${formattedTimestamp}</div>`;
    
    chatViewDiv.appendChild(receiveMsgDiv);
	chatViewDiv.scrollTop = chatViewDiv.scrollHeight;  // 새로운 메세지가 올 때마다 스크롤을 아래로
}

	




// 단체한테 보내는 이미지
function printReceiveImg(receiveImg) {
	let receiveMsgDiv = document.createElement('div');
	receiveMsgDiv.classList.add('receiveMsg', 'my-2');
	receiveMsgDiv.innerHTML = `
<div class="msgId">${receiveImg.msgDname}</div>
<img src="${receiveImg.msgContent}" class="msgBox p-2"></img>
`;
	chatViewDiv.appendChild(receiveMsgDiv);
}

// 개인으로 보내는 이미지
function printPrivateImg(privateImg) {
	let receiveMsgDiv = document.createElement('div');
	receiveMsgDiv.classList.add('privatMsg', 'my-2');
	receiveMsgDiv.innerHTML = `
<div class="msgId">${privateImg.msgDname}</div>
<img src="${privateImg.msgContent}" class="msgBox p-2"></img>
`;
	chatViewDiv.appendChild(receiveMsgDiv);
}



/* 접속중인 채팅 유저 목록 갱신 */
function refreshUserList(chatUserList) {
	// 채팅 유저 목록(접속자 목록) DIV 선택
	const userListDiv = document.getElementById('userList');
	userListDiv.innerHTML = ""; // 현재 목록 삭제
	for (let userDname of chatUserList) {
		let userDnameEl = document.createElement('button');
		userDnameEl.classList.add('btn', 'btn-success', 'btn-sm', 'm-2', 'fw-bold');
		userDnameEl.innerText = userDname;
		if (userDname === 'admin') { // 관리자 식별자
			userDnameEl.classList.add('btn-danger'); // 관리자 스타일 적용
		}
		userDnameEl.addEventListener('click', function () {
			sendTargetDiv.innerHTML = `<button class="btn btn-primary" id="sendTargetMember" onclick="this.remove()" value ="${userDname}">${userDname}</button>`;
		});
		userListDiv.appendChild(userDnameEl);
	}
}