package com.kbbukopin.webdash.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kbbukopin.webdash.dto.JwtResponse;
import com.kbbukopin.webdash.dto.LoginRequest;
import com.kbbukopin.webdash.dto.RefreshTokenRequest;
import com.kbbukopin.webdash.dto.RegisterRequest;
import com.kbbukopin.webdash.dto.ResponseHandler;
import com.kbbukopin.webdash.entity.User;
import com.kbbukopin.webdash.security.jwt.JwtUtils;
import com.kbbukopin.webdash.security.services.UserDetailsImpl;
import com.kbbukopin.webdash.security.services.UserDetailsServiceImpl;
import com.kbbukopin.webdash.services.user.UserService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            String token = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefresJwtToken(authentication);
            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            
            JwtResponse result = new JwtResponse(principal.getUsername(), principal.getRoles(),
                    token, refreshToken);
            return ResponseHandler.generateResponse("Login Success", HttpStatus.OK, result);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {

        try {
            User myUser = new User();
            myUser.setId(request.getUsername());
            myUser.setPassword(passwordEncoder.encode(request.getPassword()));
            myUser.setRoles(request.getRoles());
            User created = userService.create(myUser);
            return ResponseHandler.generateResponse("Register Success", HttpStatus.OK, created);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.MULTI_STATUS, null);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        boolean valid = jwtUtils.validateJwtToken(token);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String username = jwtUtils.getUserNameFromJwtToken(token);
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, null,
                userDetailsImpl.getAuthorities());
        String newToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefresJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(username, userDetailsImpl.getRoles(),
                newToken, refreshToken));
    }
}
