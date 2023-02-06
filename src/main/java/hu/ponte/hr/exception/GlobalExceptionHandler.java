package hu.ponte.hr.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClientException.class)
    private ResponseEntity<ApiError> handleClientException(ClientException clientException) {
        return getResponse(HttpStatus.BAD_REQUEST, Errors.VALIDATION_ERROR, clientException.getMessage());
    }

    @ExceptionHandler(TechnicalException.class)
    private ResponseEntity<ApiError> handleTechnicalException(TechnicalException clientException) {
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, Errors.TECHNICAL_ERROR, clientException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiError> handleException(Exception clientException) {
        return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, Errors.GENERAL_ERROR, clientException.getMessage());
    }

    private ResponseEntity<ApiError> getResponse(HttpStatus httpStatus, Errors error, String message) {
        var apiError = new ApiError(error.toString(), Instant.now(), message);
        log.error("Api error response {}", apiError);
        return new ResponseEntity<>(apiError, httpStatus);
    }

}
