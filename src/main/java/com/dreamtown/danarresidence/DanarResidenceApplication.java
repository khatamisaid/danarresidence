package com.dreamtown.danarresidence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dreamtown.danarresidence.config.PasswordEncode;

@SpringBootApplication
public class DanarResidenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DanarResidenceApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncode();
	}
}
