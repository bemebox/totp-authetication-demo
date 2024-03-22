package com.beom.api.totp.demo.exception;

/**
 * Thrown when user not updated exception error occurs in the application.
 *
 * @author beom
 * @since: 2024/03/16
 * @see RuntimeException
 */
public class UserNotUpdatedException extends RuntimeException {

    public UserNotUpdatedException(String message) {
        super(message);
    }

    public UserNotUpdatedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}