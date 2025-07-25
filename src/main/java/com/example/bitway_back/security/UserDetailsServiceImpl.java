package com.example.bitway_back.security;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.domain.user.UserMapping;
import com.example.bitway_back.exception.ErrorCode;
import com.example.bitway_back.repository.user.UserMappingRepository;
import com.example.bitway_back.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMappingRepository userMappingRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        // User Mapping 정보
        UserMapping userMapping = userMappingRepository.findByUuid(uuid).orElseThrow(
                () -> new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMsg()));

        User user = userRepository.findById(userMapping.getUserId()).orElseThrow(
                () -> new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMsg()));

        return new UserDetailsImpl(user);
    }
}
