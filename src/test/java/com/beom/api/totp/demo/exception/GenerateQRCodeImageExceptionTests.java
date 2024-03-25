package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link GenerateQRCodeImageException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class GenerateQRCodeImageExceptionTests {
    @Test
    void testGenerateQRCodeImageExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        GenerateQRCodeImageException exception = new GenerateQRCodeImageException(message, cause);

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
    void testGenerateQRCodeImageExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        GenerateQRCodeImageException exception = new GenerateQRCodeImageException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
