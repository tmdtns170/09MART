package com.iciafinally.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iciafinally.domain.Notice;
import com.iciafinally.repository.NoticeRepository;

@Service
public class NoticeService {
	
	@Autowired
	private NoticeRepository noticeRepository;

	public void registNotice(Notice notice, MultipartFile[] boardFiles) throws IllegalStateException, IOException {
		System.out.println("공지사항 서비스 호출");
		String fileNameList = null;

		for (MultipartFile boardFile : boardFiles) {

			if (!boardFile.isEmpty()) {
				String savePath = "I:\\bootWorkspace\\finallyProject\\src\\main\\resources\\static\\noticeImg";
				/* 파일명 생성 */
				String bfileName = UUID.randomUUID().toString();

				String originFilename = boardFile.getOriginalFilename(); // 원본 파일명
				int suffixIndex = originFilename.lastIndexOf("."); // 확장자의 시작 위치
				String suffixStr = originFilename.substring(suffixIndex); // 파일 확장자

				bfileName = bfileName + suffixStr;
				/* 파일 저장 */
				boardFile.transferTo(new File(savePath, bfileName));
				if (fileNameList == null) {
					fileNameList = "/noticeImg/" + bfileName;
				} else {
					fileNameList += ",/noticeImg/" + bfileName;
				}
			}
		}
		notice.setNfilename(fileNameList);
		noticeRepository.save(notice);
	}

	public List<Notice> findNoticeAll() {

		return noticeRepository.findByOrderByIdDesc();
	}

	public Notice findById(Long id) {

		return noticeRepository.findById(id).orElse(null);
	}

	public List<Notice> findAll() {

		return noticeRepository.findAll();
	}



}
