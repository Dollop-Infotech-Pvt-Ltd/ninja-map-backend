package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.TempUser;

@Repository
public interface ITempUserRepository extends JpaRepository<TempUser, String> {

	Optional<TempUser> findByPersonalInfo_Email(String personalInfo_Email);

	Optional<TempUser> findByPersonalInfo_MobileNumber(String mobileNumber);

	@Query("""
			    SELECT t FROM TempUser t
			    WHERE
			        t.personalInfo.email = :identifier
			        OR t.personalInfo.mobileNumber = :identifier
			""")
	Optional<TempUser> findByEmailOrMobile(@Param("identifier") String identifier);
}
