package com.paymybuddy.webapp.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SpringSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(auth -> auth
						.requestMatchers("/swagger-ui/**", "v3/api-docs/**", "/actuator/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(Customizer.withDefaults())
				.build();
	}
}
