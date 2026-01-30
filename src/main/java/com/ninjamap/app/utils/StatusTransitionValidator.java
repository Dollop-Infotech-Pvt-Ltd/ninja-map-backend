package com.ninjamap.app.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.ReportStatus;

@Component
public class StatusTransitionValidator {

	private static final Map<ReportStatus, Set<ReportStatus>> VALID_TRANSITIONS = new HashMap<>();

	static {
		// PENDING can transition to UNDER_REVIEW or REJECTED
		Set<ReportStatus> pendingTransitions = new HashSet<>();
		pendingTransitions.add(ReportStatus.UNDER_REVIEW);
		pendingTransitions.add(ReportStatus.REJECTED);
		VALID_TRANSITIONS.put(ReportStatus.PENDING, pendingTransitions);

		// UNDER_REVIEW can transition to RESOLVED or REJECTED
		Set<ReportStatus> underReviewTransitions = new HashSet<>();
		underReviewTransitions.add(ReportStatus.RESOLVED);
		underReviewTransitions.add(ReportStatus.REJECTED);
		VALID_TRANSITIONS.put(ReportStatus.UNDER_REVIEW, underReviewTransitions);

		// RESOLVED can transition to ARCHIVED
		Set<ReportStatus> resolvedTransitions = new HashSet<>();
		resolvedTransitions.add(ReportStatus.ARCHIVED);
		VALID_TRANSITIONS.put(ReportStatus.RESOLVED, resolvedTransitions);

		// REJECTED can transition to ARCHIVED
		Set<ReportStatus> rejectedTransitions = new HashSet<>();
		rejectedTransitions.add(ReportStatus.ARCHIVED);
		VALID_TRANSITIONS.put(ReportStatus.REJECTED, rejectedTransitions);

		// ARCHIVED is a terminal state - no transitions allowed
		VALID_TRANSITIONS.put(ReportStatus.ARCHIVED, new HashSet<>());
	}

	/**
	 * Validates if a status transition from currentStatus to newStatus is allowed.
	 *
	 * @param currentStatus the current status of the report
	 * @param newStatus     the desired new status
	 * @return true if the transition is valid, false otherwise
	 */
	public boolean isValidTransition(ReportStatus currentStatus, ReportStatus newStatus) {
		// Cannot transition to the same status
		if (currentStatus == newStatus) {
			return false;
		}

		// Check if the transition is in the valid transitions map
		Set<ReportStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
		return allowedTransitions != null && allowedTransitions.contains(newStatus);
	}

	/**
	 * Gets the error message for an invalid transition.
	 *
	 * @param currentStatus the current status
	 * @param newStatus     the attempted new status
	 * @return error message describing why the transition is invalid
	 */
	public String getTransitionErrorMessage(ReportStatus currentStatus, ReportStatus newStatus) {
		if (currentStatus == newStatus) {
			return String.format("Report is already in %s status", currentStatus);
		}
		return String.format("Cannot transition from %s to %s", currentStatus, newStatus);
	}
}
