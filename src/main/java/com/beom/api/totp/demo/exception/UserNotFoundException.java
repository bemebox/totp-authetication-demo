package com.beom.api.totp.demo.exception;

/**
 * Thrown when user not found exception error occurs in the application.
 *
 * @author beom
 * @since 2024/03/16
 * @see RuntimeException
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}