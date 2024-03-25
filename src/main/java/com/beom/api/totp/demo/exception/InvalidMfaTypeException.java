package com.beom.api.totp.demo.exception;

/**
 * Thrown when invalid MFA type exception error occurs in the application.
 *
 * @author beom
 * @since: 2024/03/16
 * @see RuntimeException
 */
public class InvalidMfaTypeException extends RuntimeException {

    public InvalidMfaTypeException(String message) {
        super(message);
    }

    public InvalidMfaTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}