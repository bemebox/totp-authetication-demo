package com.beom.api.totp.demo.exception;

/**
 * Thrown when an exception error occurs when generating the QRCode image.
 *
 * @author beom
 * @since 2024/03/16
 * @see RuntimeException
 */
public class GenerateQRCodeImageException extends RuntimeException {

    /**
     * constructor
     *
     * @param msg - the message of the exception
     */
    public GenerateQRCodeImageException(String msg) {
        super(msg);
    }

    /**
     * constructor
     *
     * @param msg - the message of the exception
     * @param th  - the Throwable exception
     */
    public GenerateQRCodeImageException(String msg, Throwable th) {
        super(msg, th);
    }
}