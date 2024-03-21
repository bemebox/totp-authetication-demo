package com.beom.api.totp.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TotpAuthenticationDemoApplicationTests {

	@Test
	void contextLoads() {
		String[] args = new String[] {};
		TotpAuthenticationDemoApplication.main(args);
		assertThat(args).isEmpty();
	}

}
