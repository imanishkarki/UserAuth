package com.swift.security_demo.config;

import org.springframework.stereotype.Component;

@Component
public class CharArrayToStringConverter {
    public String convert(char[] input) {
        return new String(input);
    }
}