package com.beom.api.totp.demo.service;

import com.beom.api.totp.demo.dal.MFAType;
import com.beom.api.totp.demo.dal.entity.MfaInfo;
import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.exception.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Service class for manage the account operations.
 *
 * @author beom
 * @see ITotpService
 * @since 2024/03/16
 */
@Slf4j
@Service
public class TotpService implements ITotpService {

    private final IUserService userService;

    private static final int TOTP_DIGITS = 6;
    private static final int TOTP_PERIOD = 30; // in seconds
    private static final int TOTP_SECRET_SIZE = 20; // in bytes

    private static final String TOTP_CRYPTO_ALGORITHM = "HmacSHA1";

    public TotpService(IUserService userService) {
        this.userService = userService;
    }

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

        return new Base32().encodeToString(secretKey);
    }

    @Override
    public String generateQRCodeBase64Image(String issuer, String username) throws GenerateQRCodeImageException {
        try {
            // Input validation
            if (!StringUtils.hasText(issuer)) {
                throw new IllegalArgumentException("The issuer cannot be null or empty.");
            }

            if (!StringUtils.hasText(username)) {
                throw new IllegalArgumentException("The username cannot be null or empty.");
            }

            // Getting the user secret key
            String secretKey = getTotpSecretKeyByUsername(username);
            if (!StringUtils.hasText(secretKey)) {
                throw new InvalidSecretKeyException("The User secretKey cannot be null or empty. Username: " + username);
            }

            // Generate QR Code URI
            String uri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, username, secretKey, issuer);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(uri, BarcodeFormat.QR_CODE, 200, 200);

            // Convert QR Code to BufferedImage
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert BufferedImage to Base64-encoded string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeBase64String(imageBytes);
        }catch (IllegalArgumentException | UserNotFoundException | InvalidMfaTypeException | InvalidSecretKeyException exception) {
            throw exception;
        }catch (Exception exception){
            log.error("Unable to generate the TOTP QRCode base64 image.", exception);
            throw new GenerateQRCodeImageException("Unable to generate the TOTP QRCode base64 image.", exception);
        }
    }

    @Override
    public boolean validateTOTP(String username, String inputTOTPCode) {
        try{
            // Input validation
            if (!StringUtils.hasText(username)) {
                throw new IllegalArgumentException("The secret key cannot be null or empty.");
            }

            if (!StringUtils.hasText(inputTOTPCode)) {
                throw new IllegalArgumentException("The TOTP code cannot be null or empty.");
            }

            // Retrieving TOTP secret key for the username
            String secretKey = getTotpSecretKeyByUsername(username);
            if (!StringUtils.hasText(secretKey)) {
                throw new IllegalArgumentException(
                        String.format("The secret key of the %s username cannot be null or empty.", username));
            }

            // Generating TOTP code
            long time = getCurrentEpochSecond();
            byte[] timeBytes = ByteBuffer.allocate(8).putLong(time).array();

            // Creating a secret key specification from the decoded secret key bytes
            SecretKeySpec keySpec = new SecretKeySpec(new Base32().decode(secretKey), TOTP_CRYPTO_ALGORITHM);

            // Initializing the MAC (Message Authentication Code) instance with the secret key
            Mac mac = Mac.getInstance(TOTP_CRYPTO_ALGORITHM);
            mac.init(keySpec);

            // Generating the HMAC (Hash-based Message Authentication Code) using the current time
            byte[] hash = mac.doFinal(timeBytes);

            // Extracting the offset and truncated hash from Hmac
            int offset = hash[hash.length - 1] & 0x0F;
            int truncatedHash = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16) | ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);

            // Generating the one-time password (OTP) from the truncated hash
            int otp = truncatedHash % (int) Math.pow(10, TOTP_DIGITS);
            String totpCode = String.format("%0" + TOTP_DIGITS + "d", otp);

            log.info("Given TOTP code: " + inputTOTPCode);
            log.info("Generated TOTP code: " + totpCode);

            // Comparing input TOTP code with generated TOTP code
            return inputTOTPCode.equals(totpCode);
        }catch (IllegalArgumentException | UserNotFoundException | InvalidMfaTypeException exception){
            throw exception;
        }catch (Exception exception){
            log.error("Unable to validate the TOTP code.", exception);
            throw new TotpValidationException("Unable to validate the TOTP code.", exception);
        }
    }

    /**
     * Gets the current epoch second divided by the TOTP period.
     * This method is protected for testing purposes.
     *
     * @return The current epoch second divided by the TOTP period.
     */
    protected long getCurrentEpochSecond() {
        return Instant.now().getEpochSecond() / TOTP_PERIOD;
    }

    /**
     * Retrieves the TOTP (Time-based One-Time Password) secret key associated with the specified username.
     *
     * @param username The username for which to retrieve the TOTP secret key.
     * @return The TOTP secret key.
     *
     * @throws UserNotFoundException    If no user is found with the given username.
     * @throws InvalidMfaTypeException  If no MFA (Multi-Factor Authentication) is added for the user or if no TOTP
     *                                  MFA type is added for the user.
     */
    private String getTotpSecretKeyByUsername(String username) {

        // Getting the User from database for the given username
        User user = this.userService
                .getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user found by the username: " + username));

        // User must have some MFA info type
        if (user.getMfaInfoList().isEmpty()) {
            throw new InvalidMfaTypeException("No MFA added for the user " + username);
        }

        // Search for TOTP MfaInfo type
        MfaInfo mfaInfo = user.getMfaInfoList().stream()
                .filter(info -> info.getMfaType() == MFAType.TOTP)
                .findFirst()
                .orElseThrow(() -> new InvalidMfaTypeException("No TOTP MFA type added for the user " + username));

        // Return the User TOTP secret key
        return mfaInfo.getSecretKey();
    }

}
