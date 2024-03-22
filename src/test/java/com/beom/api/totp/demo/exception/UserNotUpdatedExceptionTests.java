package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link UserNotUpdatedException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class UserNotUpdatedExceptionTests {
    @Test
    void testUserNotUpdatedExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        UserNotUpdatedException exception = new UserNotUpdatedException(message, cause);

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
    void testUserNotUpdatedExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        UserNotUpdatedException exception = new UserNotUpdatedException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
