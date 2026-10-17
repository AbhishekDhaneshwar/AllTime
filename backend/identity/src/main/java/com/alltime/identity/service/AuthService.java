package com.alltime.identity.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alltime.identity.component.JwtUtil;
import com.alltime.identity.dto.AuthResponse;
import com.alltime.identity.dto.LoginRequest;
import com.alltime.identity.dto.RegisterRequest;
import com.alltime.identity.dto.TokenRefreshRequest;
import com.alltime.identity.dto.VerifyOtpRequest;
import com.alltime.identity.entity.OtpVerification;
import com.alltime.identity.entity.RefreshToken;
import com.alltime.identity.entity.User;
import com.alltime.identity.repository.OtpVerificationRepository;

import com.alltime.identity.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpVerificationRepository otpVerificationRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            OtpVerificationRepository otpVerificationRepository,
            JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpVerificationRepository = otpVerificationRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public String registerUser(RegisterRequest registerRequest) {
        // 1. Enforce business rule: check if the phone number is already registered
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        // 2. Hash the raw password securely using BCrypt
        String hashedCondition = passwordEncoder.encode(registerRequest.getPassword());

        // 3. Map data into our model entity
        User newUser = new User();
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setPasswordHash(hashedCondition);
        newUser.setVerified(false); // Default to false: new accounts require an OTP

        // Generate a standard mock OTP code valid for 5 minutes
        String mockOtp = "123456";
        OtpVerification otp = new OtpVerification();
        otp.setOtpCode(mockOtp);
        otp.setPhoneNumber(registerRequest.getPhoneNumber());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpVerificationRepository.save(otp);

        System.out.println("=================================================");
        System.out.println("MOCK SMS FOR NUMBER: " + registerRequest.getPhoneNumber() + " | OTP: " + mockOtp);
        System.out.println("=================================================");

        // 4. Save entity state to MySQL
        userRepository.save(newUser);

        return "User registered successfully. Please verify your account with an OTP...!";

    }

    @Transactional
    public String verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        // 1. Validate if user exists
        User user = userRepository.findUserByPhoneNumber(verifyOtpRequest.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // check if user is verified
        if (user.isVerified()) {
            return "Account is already verified.";
        }

        // 2. Fetch the latest valid OTP code generated for this user
        OtpVerification activeOtp = otpVerificationRepository
                .findFirstByPhoneNumberOrderByExpiresAtDesc(verifyOtpRequest.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("No OTP requested for this phone number"));

        // 3. Evaluate compliance validations
        if (activeOtp.isExpired()) {
            throw new IllegalArgumentException("OTP has been expired,  Please request a new one...!");
        }

        if (!activeOtp.getOtpCode().equals(verifyOtpRequest.getOtpCode())) {
            throw new IllegalArgumentException("Invalid OTP code entry.");
        }

        // 4. Update core verification statuses on successful verification
        user.setVerified(true);
        userRepository.save(user);

        // Clean up verification collection records out of database
        otpVerificationRepository.deleteByPhoneNumber(verifyOtpRequest.getPhoneNumber());

        return "Account verified successfully. You can now log in.";

    }

    @Transactional
    public AuthResponse loginUser(LoginRequest loginRequest) {
        // 1. Fetch user by phone number
        User user = userRepository.findUserByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone number or password"));

        // 2. Validate password match against BCrypt hash
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid phone number or password");
        }

        // 3. Enforce business rule: user must be verified via OTP to log in
        if (!user.isVerified()) {
            throw new IllegalArgumentException("Account is not verified. Please verify using OTP first.");
        }

        // Generate Access Token and database-backed Refresh Token side-by-side
        String token = jwtUtil.generateToken(user.getPhoneNumber());
        RefreshToken refreshToken = refreshTokenService.createFreshToken(user.getPhoneNumber());
        return new AuthResponse(token, refreshToken.getToken());

    }

    @Transactional
    public AuthResponse refreshAccessToken(TokenRefreshRequest tokenrefreshRequest) {
        return refreshTokenService.findByToken(tokenrefreshRequest.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Token Rotation: Generate a fresh access token AND rotate the refresh token
                    String newAccessToken = jwtUtil.generateToken(user.getPhoneNumber());
                    RefreshToken newRefreshToken = refreshTokenService.createFreshToken(user.getPhoneNumber());
                    return new AuthResponse(newAccessToken, newRefreshToken.getToken());
                }).orElseThrow(() -> new IllegalArgumentException("Refresh token is not registered in database"));
    }

}
