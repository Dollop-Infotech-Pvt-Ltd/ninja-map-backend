package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.model.Otp;
import com.ninjamap.app.repository.IOtpRepository;
import com.ninjamap.app.service.IOtpService;

@Service
public class OtpServiceImpl implements IOtpService {

	private final IOtpRepository otpRepo;

	OtpServiceImpl(IOtpRepository otpRepo) {
		this.otpRepo = otpRepo;
	}

	private final Random random = new Random();

	@Override
	public String generateOtp(String email, OtpType otpType) {
		String otp = String.format("%06d", random.nextInt(1_000_000));
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryTime = now.plusMinutes(5);

		Otp otpEntry = otpRepo.findByEmailAndOtpType(email, otpType).orElse(null);

		if (otpEntry == null || otpEntry.getExpirationTime().isBefore(now)) {
			// Fresh OTP (or expired), reset everything
			otpEntry = Otp.builder().email(email).otp(otp).expirationTime(expiryTime).otpType(otpType).build();
		} else {
			otpEntry.setOtp(otp);
			otpEntry.setExpirationTime(expiryTime);
		}

		otpRepo.save(otpEntry);
		return otp;
	}

	@Override
	public boolean validateOtp(String email, String otp, OtpType otpType) {
		Optional<Otp> optionalOtp = otpRepo.findByEmailAndOtpType(email, otpType);
		LocalDateTime now = LocalDateTime.now();
		if (optionalOtp.isEmpty())
			return false;

		Otp otpEntry = optionalOtp.get();
		if (otpEntry.getExpirationTime().isBefore(now))
			return false;

		// Check OTP match
		boolean isValid = otpEntry.getOtp().equals(otp);

		if (isValid) {
			// Expire OTP immediately after successful verification
			otpEntry.setExpirationTime(now.minusSeconds(1));
			otpRepo.save(otpEntry);
		}

		return isValid;
	}

}
