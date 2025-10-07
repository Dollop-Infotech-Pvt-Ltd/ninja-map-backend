package com.ninjamap.app.service;

import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;

public interface IOutboxNotificationService {

//	void saveToOutbox(NotificationRequest request, OutboxNotificationStatus status);

	void saveToOutbox(String payload, OutboxType type, OutboxNotificationStatus status);

}
