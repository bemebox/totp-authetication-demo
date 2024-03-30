package com.beom.api.totp.demo;

import com.beom.api.totp.demo.dal.MFAType;
import com.beom.api.totp.demo.dal.entity.MfaInfo;
import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.service.IUserService;
import com.beom.api.totp.demo.service.TotpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@Slf4j
@SpringBootApplication
public class TotpAuthenticationDemoApplication implements CommandLineRunner {

	private final IUserService userService;

	@Autowired
	public TotpAuthenticationDemoApplication(IUserService userService) {
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(TotpAuthenticationDemoApplication.class, args);
	}

	@Override
	public void run(String... args) {

		User user = this.userService
				.createUser(createUser())
				.orElseThrow();

		log.info(user.toString());
	}

	private User createUser() {
		User user = new User();
		user.setUsername("JohnDoe");
		user.setEmail("john.doe@test.com");
		user.setPassword("123456789");

		MfaInfo mfaInfo = new MfaInfo();
		mfaInfo.setMfaType(MFAType.TOTP);
		mfaInfo.setSecretKey(TotpService.generateSecretKey());
		mfaInfo.setUser(user); // Set the User for MfaInfo

		user.setMfaInfoList(Collections.singletonList(mfaInfo));

		return user;
	}
}
