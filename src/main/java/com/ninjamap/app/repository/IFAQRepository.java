package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ninjamap.app.model.FAQ;

public interface IFAQRepository extends JpaRepository<FAQ, String> {
	// Fetch only FAQs that are not deleted
	List<FAQ> findAllByIsDeletedFalse();

    Optional<FAQ> findByIdAndIsDeletedFalse(String id);
}
