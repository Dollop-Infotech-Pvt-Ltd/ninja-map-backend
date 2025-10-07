package com.ninjamap.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Notification;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, String> {
	@Query("""
			SELECT n FROM Notification n
			WHERE (:userId IS NOT NULL AND n.user.userId = :userId)
			   OR (:adminId IS NOT NULL AND n.admin.adminId = :adminId)
			ORDER BY n.createdDate DESC
			""")
	List<Notification> findByUserOrAdmin(@Param("userId") String userId, @Param("adminId") String adminId);

}
