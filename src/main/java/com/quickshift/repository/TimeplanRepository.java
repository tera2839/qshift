package com.quickshift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quickshift.entity.Store;
import com.quickshift.entity.Timeplan;

@Repository
public interface TimeplanRepository extends JpaRepository<Timeplan, Long>{
	Timeplan findByPlanId(Long id);
	List<Timeplan> findByStore(Store store);
	void deleteAllByStore(Store store);
	
	@Modifying
	@Query("UPDATE Timeplan t set t.planName = :planName, "
			+ "t.fromTime = :fromTime, "
			+ "t.toTime = :toTime "
			+ "WHERE t.planId = :planId")
	void updateName(
			@Param("planId") Long planId,
			@Param("planName") String planName,
			@Param("toTime") String toTime,
			@Param("fromTime") String fromTime
			);
}