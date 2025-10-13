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
     * Find admin by email, optionally filtering by active status. Deleted admins are ignored.
     */
    @Query("SELECT a FROM Admin a " +
           "WHERE a.email = :email " +
           "AND a.isDeleted = false " +
           "AND (:isActive IS NULL OR a.isActive = :isActive)")
    Optional<Admin> findByEmailAndOptionalIsActive(@Param("email") String email, @Param("isActive") Boolean isActive);

    /**
     * Find admin by adminId, optionally filtering by active status. Deleted admins are ignored.
     */
    @Query("SELECT a FROM Admin a " +
           "WHERE a.adminId = :id " +
           "AND a.isDeleted = false " +
           "AND (:isActive IS NULL OR a.isActive = :isActive)")
    Optional<Admin> findByAdminIdAndOptionalIsActive(@Param("id") String id, @Param("isActive") Boolean isActive);

    /**
     * Find admins with search filter (firstName, lastName, email, mobileNumber), excluding deleted admins.
     */
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

    /**
     * Find admin by email, ignoring isDeleted (used for reactivation or other logic).
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Find admin by email OR mobile number, excluding deleted admins.
     */
    Optional<Admin> findByEmailOrMobileNumberAndIsDeletedFalse(String email, String mobileNumber);

    /**
     * Find admin by employeeId, optionally filtering by active status. Deleted admins are ignored.
     */
    Optional<Admin> findByEmployeeIdAndIsDeletedFalseAndIsActive(String employeeId, Boolean isActive);

    /**
     * Check if email exists (excluding deleted admins).
     */
    boolean existsByEmailAndIsDeletedFalse(String email);

    /**
     * Check if mobile number exists (excluding deleted admins).
     */
    boolean existsByMobileNumberAndIsDeletedFalse(String mobileNumber);
}
