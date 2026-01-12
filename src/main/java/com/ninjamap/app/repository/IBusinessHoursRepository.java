package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.Weekday;
import com.ninjamap.app.model.BusinessHours;

@Repository
public interface IBusinessHoursRepository extends JpaRepository<BusinessHours, String> {

	List<BusinessHours> findByBusinessId(String businessId);

	Optional<BusinessHours> findByBusinessIdAndWeekday(String businessId, Weekday weekday);

	void deleteByBusinessId(String businessId);
}
