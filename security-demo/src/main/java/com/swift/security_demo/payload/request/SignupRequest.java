package com.swift.security_demo.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder

public class SignupRequest {
    private Long id;
    private String name;
    private String phoneNumber;
    @Email
    @NotBlank
    private String email;
    private String username;
    private String password;

}
