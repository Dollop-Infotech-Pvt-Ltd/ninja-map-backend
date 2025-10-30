package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.PermissionType;
import com.ninjamap.app.model.Permission;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, String> {

	// Check if a permission with the same resource and action exists
	boolean existsByResourceAndActionAndIsDeletedFalse(String resource, String action);

	@Query("SELECT p FROM Permission p WHERE p.permissionId = :permissionId " + "AND p.isDeleted = false "
			+ "AND (:isActive IS NULL OR p.isActive = :isActive)")
	Optional<Permission> findByIdAndOptionalIsActive(@Param("permissionId") String permissionId,
			@Param("isActive") Boolean isActive);

	List<Permission> findAllByPermissionIdInAndIsDeletedFalse(List<String> ids);

	@Query("""
			SELECT p FROM Roles r
			JOIN r.permissions p
			WHERE r.roleId = :roleId
			  AND p.isDeleted = false
			  AND (:resource IS NULL OR LOWER(p.resource) = LOWER(:resource))
			  AND (:types IS NULL OR p.type IN :types)
			""")
	List<Permission> findAllByRoleIdAndOptionalFilters(@Param("roleId") String roleId,
			@Param("resource") String resource, @Param("types") List<PermissionType> types);

	@Query("SELECT p FROM Permission p WHERE LOWER(p.resource) = LOWER(:resource) AND p.isDeleted = false")
	List<Permission> findAllByResourceAndIsDeletedFalse(@Param("resource") String resource);

	@Query("SELECT p FROM Permission p WHERE LOWER(p.resource) = LOWER(:resource) AND p.type IN :types AND p.isDeleted = false")
	List<Permission> findByResourceAndTypeInAndIsDeletedFalse(@Param("resource") String resource,
			@Param("types") List<PermissionType> types);

	@Query("SELECT p FROM Permission p WHERE LOWER(p.resource) = LOWER(:resource) AND p.action IN :actions AND p.isDeleted = false")
	List<Permission> findByResourceAndActionInAndIsDeletedFalse(@Param("resource") String resource,
			@Param("actions") List<String> actions);

	@Query("SELECT p FROM Permission p " + "WHERE p.isDeleted = false "
			+ "AND (:resource IS NULL OR LOWER(p.resource) = LOWER(:resource)) "
			+ "AND (:types IS NULL OR p.type IN :types) "
			+ "AND (:searchKeyword IS NULL OR LOWER(p.action) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
			+ "                     OR LOWER(p.resource) LIKE LOWER(CONCAT('%', :searchKeyword, '%')))")
	List<Permission> findAllWithOptionalFilters(@Param("resource") String resource,
			@Param("types") List<PermissionType> types, @Param("searchKeyword") String searchKeyword);

}
