package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.model.User;

@Repository
public interface ISessionRepository extends JpaRepository<Session, String> {

	/**
	 * Find a session by its refresh token.
	 */
	Optional<Session> findByRefreshToken(String refreshToken);

	/**
	 * Find a session by its access token.
	 */
	Optional<Session> findByAccessToken(String accessToken);

	/**
	 * Retrieve all sessions associated with a specific user.
	 */
	List<Session> findAllByUser(User user);

	/**
	 * Retrieve all sessions associated with a specific admin.
	 */
	List<Session> findAllByAdmin(Admin admin);

	/**
	 * Delete a session by its access token.
	 */
	void deleteByAccessToken(String accessToken);

	/**
	 * Delete all sessions for a user except the specified session.
	 */
	void deleteByUserAndIdNot(User user, String sessionId);

	/**
	 * Delete all sessions for an admin except the specified session.
	 */
	void deleteByAdminAndIdNot(Admin admin, String sessionId);
}
