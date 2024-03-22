package com.beom.api.totp.demo.exception;

/**
 * Thrown when user not created exception error occurs in the application.
 *
 * @author beom
 * @since: 2024/03/16
 * @see RuntimeException
 */
public class UserNotCreatedException extends RuntimeException {

    public UserNotCreatedException(String message) {
        super(message);
    }

    public UserNotCreatedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}