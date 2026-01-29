package com.ninjamap.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.ninjamap.app.model.RoutingSearchHistory;

@Repository
public interface IRoutingSearchHistoryRepository extends JpaRepository<RoutingSearchHistory, String> {

	@Query("Select r From RoutingSearchHistory r where r.userId = :userId")
	Page<RoutingSearchHistory> findByUserId(@Param("userId") String userId, Pageable pageable);
}
