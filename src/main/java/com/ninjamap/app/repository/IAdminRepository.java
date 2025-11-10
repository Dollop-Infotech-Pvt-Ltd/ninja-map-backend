package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Admin;

@Repository
public interface IAdminRepository extends JpaRepository<Admin, String> {

	/**
	 * Find admin by email, optionally filtering by active status. Deleted admins
	 * are ignored.
	 */
	@Query("SELECT a FROM Admin a " + "WHERE a.personalInfo.email = :email " + "AND a.isDeleted = false "
			+ "AND (:isActive IS NULL OR a.isActive = :isActive)")
	Optional<Admin> findByEmailAndOptionalIsActive(@Param("email") String email, @Param("isActive") Boolean isActive);

	Optional<Admin> findByPersonalInfo_Email(String personalInfo_Email);

	/**
	 * Find admin by adminId, optionally filtering by active status. Deleted admins
	 * are ignored.
	 */
	@Query("SELECT a FROM Admin a " + "WHERE a.adminId = :id " + "AND a.isDeleted = false "
			+ "AND (:isActive IS NULL OR a.isActive = :isActive)")
	Optional<Admin> findByAdminIdAndOptionalIsActive(@Param("id") String id, @Param("isActive") Boolean isActive);

	/**
	 * Find admins with search filter (firstName, lastName, email, mobileNumber),
	 * excluding deleted admins.
	 */
	@Query("""
			    SELECT a FROM Admin a
			    WHERE a.isDeleted = false
			    AND (
			        COALESCE(:searchValue, '') = ''
			        OR LOWER(a.personalInfo.firstName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			        OR LOWER(a.personalInfo.lastName)  LIKE LOWER(CONCAT('%', :searchValue, '%'))
			        OR LOWER(a.personalInfo.email)     LIKE LOWER(CONCAT('%', :searchValue, '%'))
			        OR a.personalInfo.mobileNumber     LIKE CONCAT('%', :searchValue, '%')
			    )
			    ORDER BY a.createdDate DESC
			""")
	Page<Admin> findAllByFilters(@Param("searchValue") String searchValue, Pageable pageable);

	/**
	 * Find admin by email, ignoring isDeleted (used for reactivation or other
	 * logic).
	 */
	Optional<Admin> findByPersonalInfoEmail(String email);

	/**
	 * Find admin by email OR mobile number, excluding deleted admins.
	 */
	@Query("""
			SELECT a FROM Admin a
			WHERE a.isDeleted = false
			AND (a.personalInfo.email = :email OR a.personalInfo.mobileNumber = :mobileNumber)
			""")
	Optional<Admin> findByEmailOrMobileNumberAndIsDeletedFalse(@Param("email") String email,
			@Param("mobileNumber") String mobileNumber);

	/**
	 * Find admin by employeeId, optionally filtering by active status. Deleted
	 * admins are ignored.
	 */
	Optional<Admin> findByEmployeeIdAndIsDeletedFalseAndIsActive(String employeeId, Boolean isActive);

	/**
	 * Check if email exists (excluding deleted admins).
	 */
	boolean existsByPersonalInfoEmailAndIsDeletedFalse(String email);

	/**
	 * Check if mobile number exists (excluding deleted admins).
	 */
	boolean existsByPersonalInfoMobileNumberAndIsDeletedFalse(String mobileNumber);
}
