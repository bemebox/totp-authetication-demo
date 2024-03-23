package com.beom.api.totp.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TotpService} tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
class TotpServiceTests {

    private static final int TOTP_SECRET_SIZE = 20; // in bytes

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() { }

    @Test
    void testGenerateSecretKey() {
        // GIVEN

        // WHEN
        String secretKey = TotpService.generateSecretKey();

        // THEN
        assertThat(secretKey)
                .isNotNull();
    }

}
