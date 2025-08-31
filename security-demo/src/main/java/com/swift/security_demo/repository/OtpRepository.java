package com.swift.security_demo.repository;

import com.swift.security_demo.entity.OtpEntity;
import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.enums.OtpStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository <OtpEntity, Long> {
    Optional<OtpEntity> findById(Long id);

    Optional<OtpEntity> findTopByUserEntity_IdOrderByCreatedAtDesc(Long userId);

    List<OtpEntity> findByStatusAndCreatedAtBefore(OtpStatusEnum status, LocalDateTime time);


}
