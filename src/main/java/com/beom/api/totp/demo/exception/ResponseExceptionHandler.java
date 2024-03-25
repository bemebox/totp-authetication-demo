package com.beom.api.totp.demo.exception;

import com.beom.api.totp.demo.dal.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Class which handles all controller exceptions in one place
 *
 * @author beom
 * @since 2024/03/16
 */
@Slf4j
@ControllerAdvice
public class ResponseExceptionHandler {
    private static final String ERROR_PREFIX = "Error: {}";

    @ExceptionHandler(InvalidMfaTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMfaTypeException(InvalidMfaTypeException exception) {
        log.error(ERROR_PREFIX, "Invalid MFA type exception!", exception);

        return generateErrorResponse(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception) {
        log.error(ERROR_PREFIX, "User not found exception!", exception);

        return generateErrorResponse(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentException) {

        String exceptionMessage = Optional.ofNullable(methodArgumentException.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("Invalid request body. Please check your request and try again.");

        log.error(ERROR_PREFIX, exceptionMessage.trim());
        return generateErrorResponse(HttpStatus.BAD_REQUEST, List.of(exceptionMessage.trim()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception exception) {
        log.error(ERROR_PREFIX, "Unexpected error has occurred!", exception);

        return generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Unexpected error has occurred."));
    }

    private ResponseEntity<ErrorResponse> generateErrorResponse(HttpStatus httpStatus, List<String> errors) {

        return new ResponseEntity<>(
                new ErrorResponse(httpStatus.value(), errors, Instant.now()),
                httpStatus);
    }
}
