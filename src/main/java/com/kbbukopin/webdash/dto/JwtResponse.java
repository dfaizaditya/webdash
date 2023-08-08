package com.kbbukopin.webdash.dto;

import java.io.Serializable;
import java.util.Set;

import com.kbbukopin.webdash.entity.Role;

import lombok.Data;

@Data
public class JwtResponse implements Serializable {
    private String username;
    private Set<Role> roles;
    private String type = "Bearer";
    private String token;
    private String refreshToken;

    public JwtResponse(
            String username,
            Set<Role> roles,
            String accessToken,
            String refreshToken) {
        this.username = username;
        this.roles = roles;
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }
}
