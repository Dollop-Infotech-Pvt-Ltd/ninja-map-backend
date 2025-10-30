package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.TempUser;


@Repository
public interface ITempUserRepository extends JpaRepository<TempUser, String> {
	
	Optional<TempUser> findByPersonalInfo_Email(String personalInfo_Email);
}
