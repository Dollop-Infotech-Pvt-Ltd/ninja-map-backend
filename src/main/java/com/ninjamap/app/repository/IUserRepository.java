package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

//	Optional<User> findByEmailAndIsActiveAndIsDeletedFalse(String email, Boolean isActive);

	/**
	 * Find user by email and isDeleted = false, optionally filter by isActive. If
	 * isActive is null, it will ignore the isActive check.
	 */
	@Query("SELECT u FROM User u WHERE u.personalInfo.email = :email AND u.isDeleted = false AND (:isActive IS NULL OR u.isActive = :isActive)")
	Optional<User> findByEmailAndOptionalIsActive(@Param("email") String email, @Param("isActive") Boolean isActive);

	@Query("SELECT u FROM User u WHERE u.userId = :id AND u.isDeleted = false AND (:isActive IS NULL OR u.isActive = :isActive)")
	Optional<User> findByIdAndOptionalIsActive(@Param("id") String id, @Param("isActive") Boolean isActive);

	@Query("SELECT u FROM User u WHERE u.personalInfo.mobileNumber = :mobileNumber AND u.isDeleted = false AND (:isActive IS NULL OR u.isActive = :isActive)")
	Optional<User> findByMobileNumerAndOptionalIsActive(@Param("mobileNumber") String mobileNumber,
			@Param("isActive") Boolean isActive);

	@Query("""
			SELECT u FROM User u
			WHERE u.isDeleted = false
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(u.personalInfo.firstName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(u.personalInfo.lastName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(u.personalInfo.email) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR u.personalInfo.mobileNumber LIKE CONCAT('%', :searchValue, '%')
			)
			ORDER BY u.createdDate DESC
			""")
	Page<User> findAllByFilters(@Param("searchValue") String searchValue, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.personalInfo.mobileNumber = :mobileNumber AND u.isDeleted = false AND (:isActive IS NULL OR u.isActive = :isActive)")
	Optional<User> findByMobileNumberAndOptionalIsActive(@Param("mobileNumber") String mobileNumber,
			@Param("isActive") Boolean isActive);

	Optional<User> findByPersonalInfo_Email(String email);

	// Check if email or mobile already exists (active users)
	@Query("SELECT u FROM User u WHERE (u.personalInfo.email = :email OR u.personalInfo.mobileNumber = :mobileNumber) AND u.isDeleted = false")
	Optional<User> findByEmailOrMobileNumberAndIsDeletedFalse(@Param("email") String email,
			@Param("mobileNumber") String mobileNumber);

	Optional<User> findByUserIdAndIsDeletedFalse(String id);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.personalInfo.email = :email AND u.isDeleted = false")
	boolean existsByEmailAndIsDeletedFalse(@Param("email") String email);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.personalInfo.mobileNumber = :mobileNumber AND u.isDeleted = false")
	boolean existsByMobileNumberAndIsDeletedFalse(@Param("mobileNumber") String mobileNumber);

	@Query("""
			    SELECT u FROM User u
			    WHERE u.isDeleted = false
			      AND (
			          u.personalInfo.email = :identifier
			          OR u.personalInfo.mobileNumber = :identifier
			      )
			      AND (:isActive IS NULL OR u.isActive = :isActive)
			""")
	Optional<User> findByEmailOrMobileAndOptionalIsActive(@Param("identifier") String identifier,
			@Param("isActive") Boolean isActive);

}
