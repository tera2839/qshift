package com.quickshift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "shift")
public class Shift {
	
	@Id
	private Long id;
	
	private String year;
	
	private String month;
	
	private String date;
	
	private String planName;
	
	private String fromTime;
	
	private String toTime;
	
	private String memberName;
	
	@ManyToOne
	@JoinColumn(name = "store_id", referencedColumnName = "storeId")
	private Store store;
}
