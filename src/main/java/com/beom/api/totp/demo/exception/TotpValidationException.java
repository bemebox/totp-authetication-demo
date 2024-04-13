package com.beom.api.totp.demo.exception;

/**
 * Thrown when an exception error occurs when validation the TOTP code.
 *
 * @author beom
 * @since 2024/03/16
 * @see RuntimeException
 */
public class TotpValidationException extends RuntimeException {

    /**
     * constructor
     *
     * @param msg - the message of the exception
     */
    public TotpValidationException(String msg) {
        super(msg);
    }

    /**
     * constructor
     *
     * @param msg - the message of the exception
     * @param th  - the Throwable exception
     */
    public TotpValidationException(String msg, Throwable th) {
        super(msg, th);
    }
}