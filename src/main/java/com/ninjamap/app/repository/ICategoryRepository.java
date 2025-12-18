package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, String> {

	List<Category> findByIsActiveTrue();

	Optional<Category> findByCategoryNameAndIsDeletedFalseAndIsActiveTrue(String categoryName);
}
