package com.quickshift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quickshift.entity.Member;
import com.quickshift.entity.Store;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
	List<Member> findByStore(Store store);
	void deleteAllByStore(Store store);
	
	@Modifying
	@Query("UPDATE Member m SET m.memberName = :memberName WHERE m.memberId = :memberId")
	void updateName(@Param("memberId") Long id, @Param("memberName") String name);
}
