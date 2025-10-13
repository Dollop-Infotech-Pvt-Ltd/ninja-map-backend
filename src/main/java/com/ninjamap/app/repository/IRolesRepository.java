package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Roles;

@Repository
public interface IRolesRepository extends JpaRepository<Roles, String> {

	boolean existsByRoleNameAndIsDeletedFalse(String roleName);

	List<Roles> findAllByIsDeletedFalse();

	@Query("SELECT r FROM Roles r WHERE r.roleId = :roleId AND r.isDeleted = false "
			+ "AND (:isActive IS NULL OR r.isActive = :isActive)")
	Optional<Roles> findByRoleIdAndOptionalIsActive(@Param("roleId") String roleId,
			@Param("isActive") Boolean isActive);

	boolean existsByRoleNameAndRoleIdNotAndIsDeletedFalse(String roleName, String id);

	Optional<Roles> findByRoleIdAndIsDeletedFalse(String roleId);

	Optional<Roles> findByRoleNameAndIsActiveAndIsDeleted(String string, Boolean isActive, Boolean isDeleted);

	Optional<Roles> findByRoleName(String string);
}
