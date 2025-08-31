package com.swift.security_demo.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorCodeService {
    private final Environment environment;

    public String getMessage(String code){
        return environment.getProperty(code, "Unknown Error");
    }
}
