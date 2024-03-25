package com.beom.api.totp.demo.service;

/**
 * Interface for manage the TOTP two-factor authentication operations.
 *
 * @author beom
 * @since 2024/03/16
 */
public interface ITotpService {

    /**
     * Generates a QR code image as a Base64-encoded string for two-factor authentication apps.
     *
     * @param issuer    The issuer or provider name (e.g., company name).
     * @param username  The username associated with the account.
     * @return A Base64-encoded string representing the QR code image.
     */
    String generateQRCodeBase64Image(String issuer, String username);

}
