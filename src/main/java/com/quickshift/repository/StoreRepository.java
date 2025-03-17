package com.quickshift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickshift.entity.Admin;
import com.quickshift.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>{
	Store findByStoreId(Long id);
	List<Store> findByAdmin(Admin admin);
}