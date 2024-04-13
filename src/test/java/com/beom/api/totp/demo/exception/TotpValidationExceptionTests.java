package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TotpValidationException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class TotpValidationExceptionTests {
    @Test
    void testTotpValidationExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        TotpValidationException exception = new TotpValidationException(message, cause);

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
    void testTotpValidationExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        TotpValidationException exception = new TotpValidationException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
