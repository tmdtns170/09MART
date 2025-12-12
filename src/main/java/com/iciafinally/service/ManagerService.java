package com.iciafinally.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iciafinally.controller.VisitChart;
import com.iciafinally.domain.Manager;
import com.iciafinally.domain.Pagevisit;
import com.iciafinally.repository.ManagerRepository;
import com.iciafinally.repository.PagevisitRepository;

@Service
public class ManagerService {

	@Autowired
	private ManagerRepository managerRepository;
	@Autowired
	private PagevisitRepository pagevisitRepository;

	public Manager findByAidAndApw(String aid, String apw) {

		return managerRepository.findByAidAndApw(aid, apw);
	}

	public List<VisitChart> visitusercheak() {

		List<VisitChart> PagevisitList = new ArrayList<>();
		List<Object[]> result = pagevisitRepository.findBydate();
		for (Object[] re : result) {
			VisitChart pagevisit = new VisitChart();
			String visitdayString = (String) re[0];
			int visitea = Integer.parseInt(re[1].toString());
			pagevisit.setVisitday(visitdayString);
			pagevisit.setVisitea(visitea);
			PagevisitList.add(pagevisit);
		}

		return PagevisitList;
	}

	public Pagevisit findByVisitday(LocalDate today) {

		return pagevisitRepository.findByVisitday(today);
	}

	public void save(Pagevisit pagevisit) {
		pagevisitRepository.save(pagevisit);

	}

}