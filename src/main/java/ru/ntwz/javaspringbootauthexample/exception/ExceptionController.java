package ru.ntwz.javaspringbootauthexample.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.ntwz.javaspringbootauthexample.dto.response.ApiError;

import java.util.Collections;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(InvalidPasswordException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.UNAUTHORIZED.name());
        apiError.setReason("Provided password is invalid");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserWithSameNameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserWithSameNameAlreadyExistsException(UserWithSameNameAlreadyExistsException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.CONFLICT.name());
        apiError.setReason("User with the same name already exists");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.NOT_FOUND.name());
        apiError.setReason("Resource not found");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ApiError> handleNotAuthorizedException(NotAuthorizedException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.FORBIDDEN.name());
        apiError.setReason("User is not authorized");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage(e.getMessage());
        apiError.setStatus(HttpStatus.UNAUTHORIZED.name());
        apiError.setReason("Provided token is invalid");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setMessage("An unexpected error occurred");
        apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
        apiError.setReason("Internal server error");
        apiError.setErrors(Collections.singletonList(e.toString()));

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage("Validation error");
        apiError.setStatus(HttpStatus.BAD_REQUEST.name());
        apiError.setReason("Invalid input data");
        apiError.setErrors(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiError apiError = new ApiError();
        apiError.setMessage("Validation error");
        apiError.setStatus(HttpStatus.BAD_REQUEST.name());
        apiError.setReason("Invalid input data");
        apiError.setErrors(e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
