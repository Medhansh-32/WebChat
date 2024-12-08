package com.medhansh.webChat.repository;

import com.medhansh.webChat.model.OtpVerifiedUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifiedUserRepository extends JpaRepository<OtpVerifiedUsers,Long> {
    OtpVerifiedUsers findOtpVerifiedUsersByOtpVerifiedEmail(String otpVerifiedEmail);
}
