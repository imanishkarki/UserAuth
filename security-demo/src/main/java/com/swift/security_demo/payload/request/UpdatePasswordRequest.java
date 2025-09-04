package com.swift.security_demo.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdatePasswordRequest {
    @Schema(description = "current password")
    private char[] oldPassword;
    @Schema(description = "new password")
    private char[] newPassword;
}
