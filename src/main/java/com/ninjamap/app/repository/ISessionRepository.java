package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Session;

@Repository
public interface ISessionRepository extends JpaRepository<Session, String> {

	Optional<Session> findByRefreshToken(String refreshToken);

	Optional<Session> findByAccessToken(String accessToken);

	List<Session> findAllByAccountId(String accountId);

	void deleteByAccessToken(String accessToken);

	void deleteByAccountIdAndIdNot(String accountId, String sessionId);
}
