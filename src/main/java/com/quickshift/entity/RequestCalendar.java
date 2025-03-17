package com.quickshift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class RequestCalendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;
	
	private String requestYear;
	
	private String requestMonth;
	
	private String date;
	
	private int num;
	
	@ManyToOne
	@JoinColumn(name = "plan_id", referencedColumnName = "planId")
	private Timeplan timeplan;
	
	@ManyToOne
	@JoinColumn(name = "store_id", referencedColumnName = "storeId")
	private Store store;
}
