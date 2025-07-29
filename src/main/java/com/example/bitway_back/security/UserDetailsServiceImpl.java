package com.example.bitway_back.security;

import com.example.bitway_back.domain.user.User;
import com.example.bitway_back.exception.ErrorCode;
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
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMsg()));
        return new UserDetailsImpl(user);
    }
}
