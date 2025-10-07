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

//	Optional<Admin> findByEmailAndIsActiveAndIsDeletedFalse(String email, Boolean isActive);
//
//	Optional<Admin> findByAdminIdAndIsActiveAndIsDeletedFalse(String id, Boolean isActive);

	@Query("SELECT a FROM Admin a WHERE a.email = :email AND a.isDeleted = false AND (:isActive IS NULL OR a.isActive = :isActive)")
	Optional<Admin> findByEmailAndOptionalIsActive(@Param("email") String email, @Param("isActive") Boolean isActive);

	@Query("SELECT a FROM Admin a WHERE a.adminId = :id AND a.isDeleted = false AND (:isActive IS NULL OR a.isActive = :isActive)")
	Optional<Admin> findByAdminIdAndOptionalIsActive(@Param("id") String id, @Param("isActive") Boolean isActive);

//	Page<Admin> findAllByIsDeletedFalse(Pageable pageable);
	@Query("""
			SELECT a FROM Admin a
			WHERE a.isDeleted = false
			AND (
			    :searchValue IS NULL
			    OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(a.email) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR a.mobileNumber LIKE CONCAT('%', :searchValue, '%')
			)
			""")
	Page<Admin> findAllByFilters(@Param("searchValue") String searchValue, Pageable pageable);

	// Find admin by email ignoring isDeleted (for reactivation logic)
	Optional<Admin> findByEmail(String email);

//	boolean existsByMobileNumberAndIsDeletedFalse(String mobileNumber);

	Optional<Admin> findByEmailOrMobileNumberAndIsDeletedFalse(String email, String mobileNumber);

	// Check employeeId, optionally filtering by active status
	Optional<Admin> findByEmployeeIdAndIsDeletedFalseAndIsActive(String employeeId, Boolean isActive);

	boolean existsByEmailAndIsDeletedFalse(String email);

	boolean existsByMobileNumberAndIsDeletedFalse(String mobileNumber);

}
