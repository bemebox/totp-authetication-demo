package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link InvalidSecretKeyException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class InvalidSecretKeyExceptionTests {
    @Test
    void testInvalidSecretKeyExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        InvalidSecretKeyException exception = new InvalidSecretKeyException(message, cause);

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
    void testInvalidSecretKeyExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        InvalidSecretKeyException exception = new InvalidSecretKeyException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
