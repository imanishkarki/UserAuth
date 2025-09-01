package com.swift.security_demo.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private Object data;
    private Boolean success;
    private String message;
}
