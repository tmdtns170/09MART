package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	

	List<Notice> findByOrderByIdDesc();


}