package com.beom.api.totp.demo.service;


import com.beom.api.totp.demo.dal.MFAType;
import com.beom.api.totp.demo.dal.entity.MfaInfo;
import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.exception.GenerateQRCodeImageException;
import com.beom.api.totp.demo.exception.InvalidMfaTypeException;
import com.beom.api.totp.demo.exception.InvalidSecretKeyException;
import com.beom.api.totp.demo.exception.UserNotFoundException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
public class TotpService implements ITotpService {

    private final IUserService userService;

    private static final int TOTP_SECRET_SIZE = 20; // in bytes

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
            String secretKey = getSecretKeyByUsername(username);
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

    private String getSecretKeyByUsername(String username) {

        User user = this.userService
                .getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user found by the username: " + username));

        if (user.getMfaInfoList().isEmpty()) {
            throw new InvalidMfaTypeException("No MFA added for the user " + username);
        }

        // Search for TOTP MfaInfo
        MfaInfo mfaInfo = user.getMfaInfoList().stream()
                .filter(info -> info.getMfaType() == MFAType.TOTP)
                .findFirst()
                .orElseThrow(() -> new InvalidMfaTypeException("No TOTP MFA type added for the user " + username));

        return mfaInfo.getSecretKey();
    }

}
