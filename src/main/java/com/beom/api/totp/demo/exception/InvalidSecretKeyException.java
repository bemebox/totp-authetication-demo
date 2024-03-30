package com.beom.api.totp.demo.exception;

/**
 * Thrown when invalid secret key exception error occurs in the application.
 *
 * @author beom
 * @since 2024/03/16
 * @see RuntimeException
 */
public class InvalidSecretKeyException extends RuntimeException {

    public InvalidSecretKeyException(String message) {
        super(message);
    }

    public InvalidSecretKeyException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
