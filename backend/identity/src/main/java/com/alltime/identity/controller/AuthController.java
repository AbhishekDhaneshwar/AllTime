package com.alltime.identity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alltime.identity.dto.AuthResponse;
import com.alltime.identity.dto.LoginRequest;
import com.alltime.identity.dto.RegisterRequest;
import com.alltime.identity.dto.TokenRefreshRequest;
import com.alltime.identity.dto.VerifyOtpRequest;
import com.alltime.identity.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String message = authService.registerUser(registerRequest);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            String message = ex.getMessage();
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        try {
            String message = authService.verifyOtp(verifyOtpRequest);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            String message = ex.getMessage();
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.loginUser(loginRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        try {
            AuthResponse authResponse = authService.refreshAccessToken(tokenRefreshRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
