package com.iciafinally.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iciafinally.domain.Manager;

public interface ManagerRepository extends JpaRepository<Manager,Long> {

	List<Manager> findById(long id);

	Manager findByAidAndApw(String aid, String apw);
	
	
}