package com.clinic.clinic.security;

import com.clinic.clinic.entity.User;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return loadUserByEmail(email);
    }

    public CustomUserDetails loadUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                MessageConstants.USER_NOT_FOUND_EMAIL.formatted(email)
                        )
                );

        return new CustomUserDetails(user);
    }
}