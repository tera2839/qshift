package com.quickshift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickshift.entity.RequestCalendar;
import com.quickshift.entity.Store;

@Repository
public interface RequestCalendarRepository extends JpaRepository<RequestCalendar, Long>{

	public List<RequestCalendar> findByStore(Store store);
}