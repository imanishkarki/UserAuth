package com.swift.security_demo.entity;
import com.swift.security_demo.enums.OtpStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String otp;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    private OtpStatusEnum status;


    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.expiresAt)||this.status == OtpStatusEnum.EXPIRED;
    }
    public void markExpired(){
        this.status = OtpStatusEnum.EXPIRED;
    }
}
