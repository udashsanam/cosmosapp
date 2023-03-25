package com.cosmos.common.security.config;
import com.cosmos.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import com.cosmos.common.security.JwtTokenFilterConfigurer;
import com.cosmos.common.security.JwtTokenProvider;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private CorsFilter crosFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilterBefore(crosFilter, ChannelProcessingFilter.class);
		// Disable CSRF (cross site request forgery)
		http.csrf().disable();

		// No session will be created or used by spring security
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Entry points
		/*
		http.authorizeRequests()
				.antMatchers("/api/signin", "/api/user/**",
						"/api/reset-password","/api/save-password","/api/validate-otp",
						"/api/verify-email", "/", "/api/public/**").permitAll()
				.antMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN")
				.antMatchers("/api/moderator/**", "/api/eng-question/**","/api/nep-question/**").hasAnyAuthority("ROLE_MODERATOR")
				.antMatchers("/api/astrologer/**").hasAnyAuthority("ROLE_ASTROLOGER")
//				.antMatchers("/api/user/**").hasAnyAuthority("ROLE_USER", "ROLE_MODERATOR", "ROLE_ASTROLOGER")
				.anyRequest()
				.hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_ASTROLOGER", "ROLE_USER");
		*/
		
		http.authorizeRequests()
			.antMatchers(SecurityUtils.AUTH_WHITELIST).permitAll();
		// If a user try to access a resource without having enough permissions
		http.exceptionHandling().accessDeniedPage("/accessdenied");

		// Apply JWT
		http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}