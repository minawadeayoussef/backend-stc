package com.backend.stc.exception;


import com.backend.stc.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomValidationExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationErrorResponse> validationErrors = new ArrayList<>();

        // Loop through all errors and map them to the ValidationErrorResponse
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();

            // Customizing the error messages
            if ("email".equals(fieldName)) {
                errorMessage = "Please enter a valid email address.";
            } else if ("firstName".equals(fieldName) || "lastName".equals(fieldName)) {
                errorMessage = fieldName + " is required and cannot be empty.";
            } else if ("department".equals(fieldName)) {
                errorMessage = "Department is required.";
            }

            validationErrors.add(new ValidationErrorResponse(fieldName, errorMessage));
        });

        // Return the customized validation errors as a response
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }
}
