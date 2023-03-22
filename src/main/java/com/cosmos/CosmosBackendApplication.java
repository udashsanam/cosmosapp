package com.cosmos;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.cosmos.common.storage.model.FileStorageProperties;

	@CrossOrigin
	@SpringBootApplication
	@EnableJpaAuditing
	@EnableConfigurationProperties({
			FileStorageProperties.class
	})
	@EnableAsync
	public class CosmosBackendApplication extends SpringBootServletInitializer{

		public static void main(String[] args) {
			SpringApplication.run(CosmosBackendApplication.class, args);
		}

		@Override
		protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
			return builder.sources(CosmosBackendApplication.class);
		}

		@Bean
		public BCryptPasswordEncoder bCryptPasswordEncoder() {
			return new BCryptPasswordEncoder(12);
		}

		@Bean
		public ModelMapper modelMapper() {
			return new ModelMapper();
		}
	}