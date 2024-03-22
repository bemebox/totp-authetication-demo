package com.beom.api.totp.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link UserNotCreatedException} tests class
 *
 * @author beom
 * @since 2024/03/16
 *
 */
@SpringBootTest
class UserNotCreatedExceptionTests {
    @Test
    void testUserNotCreatedExceptionWithMessageAndThrowable() {
        // GIVEN
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // WHEN
        UserNotCreatedException exception = new UserNotCreatedException(message, cause);

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
    void testUserNotCreatedExceptionWithMessage() {
        // GIVEN
        String message = "Test message";

        // WHEN
        UserNotCreatedException exception = new UserNotCreatedException(message);

        // THEN
        assertThat(exception)
                .isNotNull();

        assertThat(message)
                .isEqualTo(exception.getMessage());
    }
}
