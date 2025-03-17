package com.quickshift.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "timeplan")
public class Timeplan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long planId;
	
	private String planName;
	
	private String fromTime;
	
	private String toTime;
	
	@ManyToOne
	@JoinColumn(name = "store_id", referencedColumnName ="storeId")
	private Store store;
}