package com.ninjamap.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ninjamap.app.model.RoutingSearchHistory;

public interface IRoutingSearchHistoryRepository extends JpaRepository<RoutingSearchHistory, String> {

}
