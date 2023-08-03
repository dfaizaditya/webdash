package com.kbbukopin.webdash.security.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kbbukopin.webdash.entity.User;
import com.kbbukopin.webdash.entity.Role; // Import the Role entity

import lombok.Data;

@Data
public class UserDetailsImpl implements UserDetails {

    private String username;
    @JsonIgnore
    private String password;
    private Set<Role> roles; // Change roles to Set<Role> type

    public UserDetailsImpl(String username, String password, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
