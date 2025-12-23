package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Place;

@Repository
public interface IPlaceRepository extends JpaRepository<Place, String> {

	/**
	 * Find all places belonging to a user
	 */
	Page<Place> findByUserIdAndIsDeletedFalse(String userId,Pageable pageable);


	/**
	 * Find a specific place by ID with deletion check
	 */
	Optional<Place> findByIdAndIsDeletedFalse(String id);

	/**
	 * Check if a place belongs to a user
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.id = :placeId AND p.userId = :userId AND p.isDeleted = false")
	boolean existsByIdAndUserId(@Param("placeId") String placeId, @Param("userId") String userId);
 

	/**
	 * Check if user already has a custom place with the same name
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.userId = :userId AND p.name = :name  AND p.isDeleted = false")
	boolean existsByUserIdAndNameCustom(@Param("userId") String userId, @Param("name") String name);
	
	
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.userId = :userId AND p.name = :name  AND p.isDeleted = false  ANd p.id !=:placeId")
	boolean existsByUserIdAndNameCustom(@Param("userId") String userId, @Param("name") String name,String placeId);

}
