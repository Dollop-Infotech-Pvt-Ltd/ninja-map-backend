package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ninjamap.app.model.ContactUs;

public interface IContactUsRepository extends JpaRepository<ContactUs, String> {

	Optional<ContactUs> findByIdAndIsDeletedFalse(String id);

	Page<ContactUs> findByIsDeletedFalseAndIsActiveTrue(Pageable pageable);
}
