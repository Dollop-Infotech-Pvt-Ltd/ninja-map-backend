package com.ninjamap.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.EmailTemplate;

@Repository
public interface IEmailTemplateRepository extends JpaRepository<EmailTemplate, String> {

	Optional<EmailTemplate> findByTemplateName(String templateName);

}
