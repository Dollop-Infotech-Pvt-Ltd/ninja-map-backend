package com.ninjamap.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.BusinessImage;

@Repository
public interface IBusinessImageRepository extends JpaRepository<BusinessImage, String> {

	List<BusinessImage> findByBusinessId(String businessId);

	void deleteByBusinessId(String businessId);
}
