package com.swift.security_demo.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class BaseException extends RuntimeException{
    private String code;
    private String message;
    private HttpStatus status;

    @Override
    public String getMessage() {return message;}
}
