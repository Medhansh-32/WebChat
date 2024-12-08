package com.medhansh.webChat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Otp {
    private String otp;
    private String email;
    private LocalDateTime sendTime;
    private LocalDateTime expireTime;
}
