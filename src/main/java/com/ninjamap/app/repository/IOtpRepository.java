package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.model.Otp;

@Repository
public interface IOtpRepository extends JpaRepository<Otp, String> {
	Optional<Otp> findByEmailAndOtpType(String email, OtpType otpType);
}
