package com.beom.api.totp.demo.exception;

import com.beom.api.totp.demo.dal.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link ResponseExceptionHandler} tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ResponseExceptionHandlerTests {

    private ResponseExceptionHandler responseExceptionHandler;

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() {
        this.responseExceptionHandler = new ResponseExceptionHandler();
    }

    @Test
    void testHandleInvalidMfaTypeException() {

        //GIVEN
        InvalidMfaTypeException exception = new InvalidMfaTypeException("Invalid MFA type exception");

        //WHEN
        ResponseEntity<ErrorResponse> responseEntity = this.responseExceptionHandler.handleInvalidMfaTypeException(exception);

        //THEN
        assertThat(responseEntity)
                .as("response entity cannot be null")
                .isNotNull();

        assertThat(responseEntity.getStatusCode())
                .as("response entity status code is not " + HttpStatus.BAD_REQUEST)
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testHandleUserNotFoundException() {

        //GIVEN
        UserNotFoundException exception = new UserNotFoundException("User not found exception");

        //WHEN
        ResponseEntity<ErrorResponse> responseEntity = this.responseExceptionHandler.handleUserNotFoundException(exception);

        //THEN
        assertThat(responseEntity)
                .as("response entity cannot be null")
                .isNotNull();

        assertThat(responseEntity.getStatusCode())
                .as("response entity status code is not " + HttpStatus.BAD_REQUEST)
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void doHandleMethodArgumentNotValidException() {
        // GIVEN
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
        FieldError mockFieldError = mock(FieldError.class);
        String errorMessage = "Invalid request body. Please check your request and try again.";

        //WHEN
        when(mockException.getFieldError()).thenReturn(mockFieldError);
        when(mockFieldError.getDefaultMessage()).thenReturn(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = this.responseExceptionHandler.handleMethodArgumentNotValidException(mockException);

        // THEN
        assertThat(responseEntity.getStatusCode())
                .as("response entity status code is not " + HttpStatus.BAD_REQUEST)
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void doHandleMethodArgumentNotValidExceptionWithNullFieldError() {

        // GIVEN
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);

        //WHEN
        when(mockException.getFieldError()).thenReturn(null);

        ResponseEntity<ErrorResponse> responseEntity = this.responseExceptionHandler.handleMethodArgumentNotValidException(mockException);

        // THEN
        assertThat(responseEntity.getStatusCode())
                .as("response entity status code is not " + HttpStatus.BAD_REQUEST)
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGlobalExceptionHandler() {

        //GIVEN
        Exception exception = new Exception("undefined exception");

        //WHEN
        ResponseEntity<ErrorResponse> responseEntity = this.responseExceptionHandler.globalExceptionHandler(exception);

        //THEN
        assertThat(responseEntity)
                .as("response entity cannot be null")
                .isNotNull();

        assertThat(responseEntity.getStatusCode())
                .as("response entity status code is not " + HttpStatus.INTERNAL_SERVER_ERROR)
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
