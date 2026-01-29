package com.ninjamap.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.RoutingSearchHistory;

@Repository
public interface IRoutingSearchHistoryRepository extends JpaRepository<RoutingSearchHistory, String> {

}
