package com.beom.api.totp.demo;

import com.beom.api.totp.demo.dal.MFAType;
import com.beom.api.totp.demo.dal.entity.MfaInfo;
import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

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
	public void run(String... args) throws Exception {
		// Create a user when Spring Boot starts
		User user = createUser();
		this.userService.createUser(user);
	}

	private User createUser() {
		User user = new User();
		user.setUsername("JohnDoe");
		user.setEmail("john.doe@test.com");
		user.setPassword("123456789");

		MfaInfo mfaInfo = new MfaInfo();
		mfaInfo.setMfaType(MFAType.TOTP);
		mfaInfo.setSecretKey("123456789");
		mfaInfo.setUser(user); // Set the User for MfaInfo

		user.setMfaInfoList(Collections.singletonList(mfaInfo));

		return user;
	}
}
