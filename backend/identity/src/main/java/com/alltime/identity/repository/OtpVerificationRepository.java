package com.alltime.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alltime.identity.entity.OtpVerification;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // Dynamic Query Method to fetch the latest OTP generated for a phone number
    Optional<OtpVerification> findFirstByPhoneNumberOrderByExpiresAtDesc(String phoneNumber);

    void deleteByPhoneNumber(String phoneNumber);

}
