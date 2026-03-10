package com.ayd2.congress.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(userOpt.get().getEmail())
                .password(userOpt.get().getPassword())
                .roles(userOpt.get().getRol().getName())
                .build();
        return userDetails;
    }
}
