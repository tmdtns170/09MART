package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Message;


public interface MessageRepository extends JpaRepository<Message, Long>{

	List<Message> findByOrderById();

}
