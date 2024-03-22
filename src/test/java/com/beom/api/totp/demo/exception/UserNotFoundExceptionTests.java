package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link UserNotFoundException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class UserNotFoundExceptionTests {
    @Test
    void testUserNotFoundExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        UserNotFoundException exception = new UserNotFoundException(message, cause);

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
    void testUserNotFoundExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        UserNotFoundException exception = new UserNotFoundException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}

