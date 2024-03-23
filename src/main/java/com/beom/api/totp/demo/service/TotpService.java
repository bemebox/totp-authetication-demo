package com.beom.api.totp.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service class for manage the account operations.
 *
 * @author beom
 * @see ITotpService
 * @since 2024/03/16
 */
@Slf4j
@Service
public class TotpService {

    private static final int TOTP_SECRET_SIZE = 20; // in bytes

    /**
     * Generates a random secret key for use in Time-based One-Time Password (TOTP) authentication.
     * The secret key is typically used by the user to configure their authenticator app.
     *
     * @return A randomly generated secret key encoded in Base64 format.
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] secretKey = new byte[TOTP_SECRET_SIZE];
        random.nextBytes(secretKey);

        return new Base64().encodeAsString(secretKey);
    }

}
