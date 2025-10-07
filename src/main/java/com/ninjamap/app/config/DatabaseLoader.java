package com.ninjamap.app.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import com.ninjamap.app.repository.IEmailTemplateRepository;

import lombok.RequiredArgsConstructor;

@Component
@Configuration
@RequiredArgsConstructor
public class DatabaseLoader {

	private final IEmailTemplateRepository emailTemplateRepository;

	@Bean
	ApplicationRunner loadData(DataSource dataSource) {
		return args -> {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

			if (emailTemplateRepository.count() > 0) {
				return;
			}

			try (Connection connection = dataSource.getConnection()) {
				// Load all SQL files from the `resources/db-data/` folder
				Resource[] sqlFiles = resolver.getResources("classpath:db-data/*.sql");

				for (Resource sqlFile : sqlFiles) {
					System.out.println("Executing: " + sqlFile.getFilename());
					ScriptUtils.executeSqlScript(connection, sqlFile);
				}

				System.out.println("All SQL files executed successfully!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};
	}

}
