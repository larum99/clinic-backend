package com.clinic.clinic.security;

import com.clinic.clinic.entity.Permission;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return buildAuthorities();
    }

    private Set<GrantedAuthority> buildAuthorities() {

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getName()
                )
        );

        for (Permission permission : user.getRole().getPermissions()) {
            authorities.add(
                    new SimpleGrantedAuthority(permission.getCode())
            );
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != UserStatus.BLOQUEADO;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVO;
    }
}