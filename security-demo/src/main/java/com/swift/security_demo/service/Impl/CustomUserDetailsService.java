package com.swift.security_demo.service.Impl;

import com.swift.security_demo.entity.UserEntity;
import com.swift.security_demo.exception.BaseException;
import com.swift.security_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
      UserEntity user = userRepository.findByUsername(username)
               .orElseThrow(() -> BaseException.builder()
                       .code("IDE04")
                       .status(HttpStatus.NOT_FOUND)
                       .build());
      return user;
    }
}
