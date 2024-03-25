package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link InvalidMfaTypeException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class InvalidMfaTypeExceptionTests {
    @Test
    void testInvalidMfaTypeExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        InvalidMfaTypeException exception = new InvalidMfaTypeException(message, cause);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());

        assertThat(cause)
                .usingRecursiveComparison()
                .isEqualTo(exception.getCause());

    }

    @Test
    void testInvalidMfaTypeExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        InvalidMfaTypeException exception = new InvalidMfaTypeException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
