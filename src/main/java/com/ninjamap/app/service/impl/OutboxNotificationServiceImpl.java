package com.ninjamap.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.model.OutboxNotification;
import com.ninjamap.app.repository.IOutboxNotificationRepository;
import com.ninjamap.app.service.IOutboxNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxNotificationServiceImpl implements IOutboxNotificationService {
	private final IOutboxNotificationRepository outboxRepo;

//	@Override
//	public void saveToOutbox(NotificationRequest request, OutboxNotificationStatus status) {
//		OutboxNotification outbox = OutboxNotification.builder().payload(request.toString()).status(status).build();
//		outboxRepo.save(outbox);
//	}

	@Override
	public void saveToOutbox(String payload, OutboxType type, OutboxNotificationStatus status) {

		// Check duplicate before saving
		boolean exists = outboxRepo.existsByPayloadAndTypeAndStatusIn(payload, type,
				List.of(OutboxNotificationStatus.NEW, OutboxNotificationStatus.FAILED));

		if (exists) {
			System.out.println("Duplicate outbox entry exists for type " + type);
			return; // Skip saving duplicate
		}

		OutboxNotification outbox = OutboxNotification.builder().payload(payload).type(type).status(status).build();
		outboxRepo.save(outbox);
	}

}
