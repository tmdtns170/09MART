package com.iciafinally.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity @Getter @Setter @ToString
public class Pagevisit {

	@Id
	private LocalDate visitday;
	
	private int visitea;
	
	
	
	
	
}