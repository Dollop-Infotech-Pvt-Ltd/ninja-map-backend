package com.ninjamap.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.model.OutboxNotification;

@Repository
public interface IOutboxNotificationRepository extends JpaRepository<OutboxNotification, String> {
	List<OutboxNotification> findByStatus(String status);

	List<OutboxNotification> findByStatusIn(List<OutboxNotificationStatus> statuses);

	boolean existsByPayloadAndTypeAndStatusIn(String payload, OutboxType type, List<OutboxNotificationStatus> statuses);

}
