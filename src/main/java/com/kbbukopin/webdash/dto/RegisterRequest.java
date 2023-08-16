package com.kbbukopin.webdash.dto;

import java.io.Serializable;
import java.util.Set;

import com.kbbukopin.webdash.entity.Role;

import lombok.Data;

@Data
public class RegisterRequest implements Serializable {

    private String username;
    private String password;
    private Set<Role> roles;

}