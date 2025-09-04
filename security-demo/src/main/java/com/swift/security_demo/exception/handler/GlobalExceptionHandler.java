package com.swift.security_demo.exception.handler;

import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.utils.ErrorCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ErrorCodeService errorCodeService;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> allExceptionHandler(BaseException e) {
        ErrorResponse erp = new ErrorResponse(
                errorCodeService.getMessage(e.getCode()),
                e.getCode(),
                false) ;
        return new ResponseEntity<ErrorResponse>(erp, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> fieldValidationException(MethodArgumentNotValidException mex){
        StringBuilder errorMessage = new StringBuilder();
        mex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        });
        ErrorResponse erp = new ErrorResponse(errorMessage.toString(), null, false);
        return new ResponseEntity<ErrorResponse>(erp, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponse> mailException(MailException e) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Failed to send email. ");

        if (e.getMessage() != null) {
            errorMessage.append("Reason: ").append(e.getMessage()).append("; ");
        }

        if (e.getCause() != null) {
            errorMessage.append("Cause: ").append(e.getCause().getMessage()).append("; ");
        }

        ErrorResponse erp = new ErrorResponse(errorMessage.toString(), null, false);
        return new ResponseEntity<>(erp, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleMailAuthException(MailAuthenticationException e) {
        String errorMessage = "Email authentication failed: " + e.getMessage();
        ErrorResponse error = new ErrorResponse(errorMessage, null, false);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


}

