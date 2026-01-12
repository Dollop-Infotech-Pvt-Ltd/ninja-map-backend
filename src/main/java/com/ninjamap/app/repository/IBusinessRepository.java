package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Business;

@Repository
public interface IBusinessRepository extends JpaRepository<Business, String> {

	List<Business> findBySubCategoryId(String subCategoryId);

	Optional<Business> findByPhoneNumber(String phoneNumber);

	List<Business> findByIsActiveTrueAndIsDeletedFalse();
}
